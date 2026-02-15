'use client';

import { useQuery } from '@tanstack/react-query';
import Link from 'next/link';
import { api } from '@/lib/api';
import { Book, ApiResponse } from '@/types';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { BookOpen, Plus, Trash2, RefreshCw } from 'lucide-react';
import { Input } from '@/components/ui/input';

export default function BookshelfPage() {
  const { data: booksData, isLoading, refetch } = useQuery<ApiResponse<Book[]>>({
    queryKey: ['books'],
    queryFn: () => api.get('/api/v1/books'),
  });

  const books = booksData?.data || [];

  if (isLoading) {
    return (
      <div className="container mx-auto py-8">
        <div className="flex items-center justify-center h-64">
          <RefreshCw className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold">我的书架</h1>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={() => refetch()}>
            <RefreshCw className="h-4 w-4 mr-2" />
            刷新
          </Button>
          <Link href="/search">
            <Button size="sm">
              <Plus className="h-4 w-4 mr-2" />
              添加书籍
            </Button>
          </Link>
        </div>
      </div>

      {books.length === 0 ? (
        <div className="text-center py-16">
          <BookOpen className="h-16 w-16 mx-auto text-muted-foreground mb-4" />
          <h2 className="text-xl font-semibold mb-2">书架空空如也</h2>
          <p className="text-muted-foreground mb-4">去搜索添加一些书籍吧</p>
          <Link href="/search">
            <Button>搜索书籍</Button>
          </Link>
        </div>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
          {books.map((book) => (
            <Link key={book.id} href={`/reader/${book.id}`}>
              <Card className="overflow-hidden hover:shadow-lg transition-shadow cursor-pointer group">
                <div className="aspect-[3/4] bg-muted relative">
                  {book.coverUrl ? (
                    <img
                      src={book.coverUrl}
                      alt={book.name}
                      className="object-cover w-full h-full"
                    />
                  ) : (
                    <div className="flex items-center justify-center h-full">
                      <BookOpen className="h-12 w-12 text-muted-foreground" />
                    </div>
                  )}
                  <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                    <Button variant="destructive" size="sm">
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
                <CardContent className="p-3">
                  <h3 className="font-medium truncate text-sm">{book.name}</h3>
                  <p className="text-xs text-muted-foreground truncate">
                    {book.author || '未知作者'}
                  </p>
                  <p className="text-xs text-muted-foreground mt-1">
                    {book.durChapterIndex}/{book.totalChapterNum}章
                  </p>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}
