'use client';

import { useQuery } from '@tanstack/react-query';
import { useParams, useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';
import { api } from '@/lib/api';
import { Book, BookChapter, ApiResponse } from '@/types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useReaderStore } from '@/stores/readerStore';
import { cn } from '@/lib/utils';
import {
  ArrowLeft,
  Menu,
  Settings,
  ChevronLeft,
  ChevronRight,
  Sun,
  Moon,
  BookOpen,
} from 'lucide-react';

export default function ReaderPage() {
  const params = useParams();
  const router = useRouter();
  const bookId = Number(params.bookId);
  
  const {
    currentChapter,
    setCurrentChapter,
    scrollPosition,
    setScrollPosition,
    settings,
    updateSettings,
  } = useReaderStore();

  const contentRef = useRef<HTMLDivElement>(null);
  const [showSettings, setShowSettings] = useState(false);
  const [showToc, setShowToc] = useState(false);

  const { data: bookData } = useQuery<ApiResponse<Book>>({
    queryKey: ['book', bookId],
    queryFn: () => api.get(`/api/v1/books/${bookId}`),
  });

  const { data: chaptersData } = useQuery<ApiResponse<BookChapter[]>>({
    queryKey: ['chapters', bookId],
    queryFn: () => api.get(`/api/v1/books/${bookId}/chapters`),
  });

  const { data: contentData, refetch: refetchContent } = useQuery<ApiResponse<string>>({
    queryKey: ['chapterContent', bookId, currentChapter],
    queryFn: () => api.get(`/api/v1/books/${bookId}/chapters/${currentChapter}/content`),
    enabled: chaptersData?.data?.length > 0,
  });

  const book = bookData?.data;
  const chapters = chaptersData?.data || [];
  const content = contentData?.data || '';

  // 恢复阅读进度
  useEffect(() => {
    if (book?.durChapterIndex && currentChapter === 0) {
      setCurrentChapter(book.durChapterIndex);
    }
  }, [book, currentChapter, setCurrentChapter]);

  // 保存阅读进度
  useEffect(() => {
    const saveProgress = async () => {
      try {
        await api.put(`/api/v1/books/${bookId}/progress`, null, {
          params: { chapterIndex: currentChapter, chapterPos: scrollPosition },
        });
      } catch (error) {
        console.error('保存进度失败', error);
      }
    };

    const timer = setTimeout(saveProgress, 5000);
    return () => clearTimeout(timer);
  }, [currentChapter, scrollPosition, bookId]);

  // 滚动时更新位置
  const handleScroll = () => {
    if (contentRef.current) {
      setScrollPosition(contentRef.current.scrollTop);
    }
  };

  const goToChapter = (index: number) => {
    setCurrentChapter(index);
    setShowToc(false);
    window.scrollTo(0, 0);
  };

  const toggleTheme = () => {
    const themes: ('light' | 'dark' | 'sepia')[] = ['light', 'dark', 'sepia'];
    const currentIndex = themes.indexOf(settings.theme);
    const nextTheme = themes[(currentIndex + 1) % themes.length];
    updateSettings({ theme: nextTheme });
  };

  if (!book) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div
      className={cn(
        'min-h-screen flex flex-col',
        settings.theme === 'dark' && 'dark bg-gray-900',
        settings.theme === 'sepia' && 'bg-[#f4ecd8]'
      )}
    >
      {/* 顶部工具栏 */}
      <header className="sticky top-0 z-50 border-b bg-background/95 backdrop-blur">
        <div className="container mx-auto flex items-center justify-between h-14 px-4">
          <div className="flex items-center gap-2">
            <Button variant="ghost" size="icon" onClick={() => router.back()}>
              <ArrowLeft className="h-5 w-5" />
            </Button>
            <div className="hidden sm:block">
              <h1 className="text-sm font-medium truncate max-w-[200px]">{book.name}</h1>
              <p className="text-xs text-muted-foreground">{book.author}</p>
            </div>
          </div>

          <div className="flex items-center gap-1">
            <Button variant="ghost" size="icon" onClick={() => setShowToc(!showToc)}>
              <Menu className="h-5 w-5" />
            </Button>
            <Button variant="ghost" size="icon" onClick={toggleTheme}>
              {settings.theme === 'dark' ? (
                <Sun className="h-5 w-5" />
              ) : (
                <Moon className="h-5 w-5" />
              )}
            </Button>
            <Button variant="ghost" size="icon" onClick={() => setShowSettings(!showSettings)}>
              <Settings className="h-5 w-5" />
            </Button>
          </div>
        </div>
      </header>

      <div className="flex-1 flex">
        {/* 目录侧边栏 */}
        {showToc && (
          <aside className="w-72 border-r bg-background overflow-y-auto hidden md:block">
            <div className="p-4">
              <h2 className="font-semibold mb-4">目录</h2>
              <div className="space-y-1">
                {chapters.map((chapter, index) => (
                  <button
                    key={chapter.id}
                    onClick={() => goToChapter(index)}
                    className={cn(
                      'w-full text-left px-3 py-2 rounded text-sm truncate',
                      index === currentChapter
                        ? 'bg-primary text-primary-foreground'
                        : 'hover:bg-muted'
                    )}
                  >
                    {chapter.title || `第${index + 1}章`}
                  </button>
                ))}
              </div>
            </div>
          </aside>
        )}

        {/* 阅读内容 */}
        <main className="flex-1 overflow-hidden">
          <div
            ref={contentRef}
            onScroll={handleScroll}
            className={cn(
              'container mx-auto max-w-3xl py-8 px-4 overflow-y-auto h-[calc(100vh-120px)]',
              settings.theme === 'dark' && 'text-gray-300',
              settings.theme === 'sepia' && 'text-gray-800'
            )}
            style={{
              fontSize: `${settings.fontSize}px`,
              lineHeight: settings.lineHeight,
              fontFamily: settings.fontFamily,
            }}
          >
            {content ? (
              <div className="whitespace-pre-wrap">{content}</div>
            ) : (
              <div className="text-center py-16 text-muted-foreground">
                <BookOpen className="h-12 w-12 mx-auto mb-4" />
                <p>加载中...</p>
              </div>
            )}
          </div>

          {/* 底部翻页 */}
          <div className="fixed bottom-0 left-0 right-0 border-t bg-background/95 backdrop-blur py-2">
            <div className="container mx-auto flex items-center justify-between px-4">
              <Button
                variant="ghost"
                size="sm"
                disabled={currentChapter === 0}
                onClick={() => goToChapter(currentChapter - 1)}
              >
                <ChevronLeft className="h-4 w-4 mr-1" />
                上一章
              </Button>
              <span className="text-sm text-muted-foreground">
                {currentChapter + 1} / {chapters.length}
              </span>
              <Button
                variant="ghost"
                size="sm"
                disabled={currentChapter >= chapters.length - 1}
                onClick={() => goToChapter(currentChapter + 1)}
              >
                下一章
                <ChevronRight className="h-4 w-4 ml-1" />
              </Button>
            </div>
          </div>
        </main>
      </div>

      {/* 设置面板 */}
      {showSettings && (
        <div className="fixed inset-y-0 right-0 w-80 border-l bg-background p-4 shadow-lg z-50">
          <h2 className="font-semibold mb-4">阅读设置</h2>
          
          <div className="space-y-4">
            <div>
              <label className="text-sm font-medium">字体大小</label>
              <div className="flex items-center gap-2 mt-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => updateSettings({ fontSize: Math.max(12, settings.fontSize - 2) })}
                >
                  A-
                </Button>
                <span className="flex-1 text-center">{settings.fontSize}px</span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => updateSettings({ fontSize: Math.min(32, settings.fontSize + 2) })}
                >
                  A+
                </Button>
              </div>
            </div>

            <div>
              <label className="text-sm font-medium">行间距</label>
              <div className="flex items-center gap-2 mt-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => updateSettings({ lineHeight: Math.max(1.2, settings.lineHeight - 0.2) })}
                >
                  -
                </Button>
                <span className="flex-1 text-center">{settings.lineHeight}</span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => updateSettings({ lineHeight: Math.min(2.5, settings.lineHeight + 0.2) })}
                >
                  +
                </Button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
