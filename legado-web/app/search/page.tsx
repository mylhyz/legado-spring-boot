'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { SearchResult, ApiResponse } from '@/types';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { BookOpen, Search, Plus, RefreshCw, Loader2 } from 'lucide-react';

export default function SearchPage() {
  const router = useRouter();
  const [keyword, setKeyword] = useState('');
  const [searched, setSearched] = useState(false);

  const { data, isLoading, refetch, isFetching } = useQuery<ApiResponse<SearchResult[]>>({
    queryKey: ['search', keyword],
    queryFn: () => api.get(`/api/v1/books/search`, { params: { keyword } }),
    enabled: searched && keyword.trim().length > 0,
  });

  const results = data?.data || [];

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (keyword.trim()) {
      setSearched(true);
    }
  };

  const handleAddBook = async (result: SearchResult) => {
    try {
      await api.post('/api/v1/books/from-source', null, {
        params: {
          bookUrl: result.bookUrl,
          sourceUrl: result.sourceUrl,
        },
      });
      alert('添加成功！');
      router.push('/bookshelf');
    } catch (error) {
      alert('添加失败，请重试');
    }
  };

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6">全网搜索</h1>

      <form onSubmit={handleSearch} className="flex gap-2 mb-8">
        <Input
          type="text"
          placeholder="输入书名或作者名搜索..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="flex-1"
        />
        <Button type="submit" disabled={isLoading}>
          {isLoading ? (
            <Loader2 className="h-4 w-4 animate-spin" />
          ) : (
            <Search className="h-4 w-4 mr-2" />
          )}
          搜索
        </Button>
      </form>

      {!searched ? (
        <div className="text-center py-16">
          <Search className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
          <h2 className="text-xl font-semibold mb-2">搜索书籍</h2>
          <p className="text-muted-foreground">输入书名或作者名开始搜索</p>
        </div>
      ) : isLoading ? (
        <div className="flex items-center justify-center py-16">
          <RefreshCw className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      ) : results.length === 0 ? (
        <div className="text-center py-16">
          <BookOpen className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
          <h2 className="text-xl font-semibold mb-2">未找到相关书籍</h2>
          <p className="text-muted-foreground">试试其他关键词</p>
        </div>
      ) : (
        <div className="space-y-4">
          <p className="text-sm text-muted-foreground">
            找到 {results.length} 个结果
          </p>
          
          {results.map((result, index) => (
            <Card key={index} className="hover:shadow-md transition-shadow">
              <CardContent className="p-4">
                <div className="flex gap-4">
                  <div className="w-20 h-28 bg-muted rounded flex-shrink-0 overflow-hidden">
                    {result.coverUrl ? (
                      <img
                        src={result.coverUrl}
                        alt={result.name}
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="flex items-center justify-center h-full">
                        <BookOpen className="h-8 w-8 text-muted-foreground" />
                      </div>
                    )}
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold truncate">{result.name}</h3>
                    <p className="text-sm text-muted-foreground mt-1">
                      作者: {result.author || '未知'}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      来源: {result.sourceName}
                    </p>
                    {result.latestChapterTitle && (
                      <p className="text-sm text-muted-foreground truncate mt-1">
                        最新: {result.latestChapterTitle}
                      </p>
                    )}
                    {result.intro && (
                      <p className="text-sm text-muted-foreground line-clamp-2 mt-2">
                        {result.intro}
                      </p>
                    )}
                  </div>
                  
                  <div className="flex-shrink-0">
                    <Button size="sm" onClick={() => handleAddBook(result)}>
                      <Plus className="h-4 w-4 mr-1" />
                      添加
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
