'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { api } from '@/lib/api';
import { BookSource, ApiResponse } from '@/types';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import {
  Plus,
  Search,
  RefreshCw,
  MoreVertical,
  Pencil,
  Trash2,
  Play,
  Copy,
  Download,
  Upload,
  LinkIcon,
} from 'lucide-react';

export default function SourcesPage() {
  const router = useRouter();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedGroup, setSelectedGroup] = useState<string>('all');
  const [showImportUrlDialog, setShowImportUrlDialog] = useState(false);
  const [importUrl, setImportUrl] = useState('');

  const { data, isLoading, refetch } = useQuery<ApiResponse<BookSource[]>>({
    queryKey: ['sources'],
    queryFn: () => api.get('/api/v1/sources'),
  });

  const sources = data?.data || [];

  const filteredSources = sources.filter((source) => {
    const matchesSearch = source.sourceName.toLowerCase().includes(searchKeyword.toLowerCase()) ||
      source.sourceGroup?.toLowerCase().includes(searchKeyword.toLowerCase());
    const matchesGroup = selectedGroup === 'all' || source.sourceGroup === selectedGroup;
    return matchesSearch && matchesGroup;
  });

  const groups = [...new Set(sources.map((s) => s.sourceGroup).filter(Boolean))];

  const handleTestSource = async (sourceId: number) => {
    try {
      await api.post(`/api/v1/sources/${sourceId}/test`);
      alert('书源测试成功');
    } catch (error) {
      alert('书源测试失败');
    }
  };

  const handleDeleteSource = async (sourceId: number) => {
    if (!confirm('确定要删除这个书源吗？')) return;
    try {
      await api.delete(`/api/v1/sources/${sourceId}`);
      refetch();
    } catch (error) {
      alert('删除失败');
    }
  };

  const handleImportSource = async () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.json';
    input.onchange = async (e) => {
      const file = (e.target as HTMLInputElement).files?.[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = async (event) => {
        try {
          const content = event.target?.result as string;
          await api.post('/api/v1/sources/batch', JSON.parse(content));
          alert('导入成功');
          refetch();
        } catch {
          alert('导入失败');
        }
      };
      reader.readAsText(file);
    };
    input.click();
  };

  const handleImportFromUrl = async () => {
    if (!importUrl.trim()) {
      alert('请输入URL');
      return;
    }
    try {
      const response = await api.post<ApiResponse<number>>(`/api/v1/sources/import/url?url=${encodeURIComponent(importUrl)}`);
      if (response.data.code === 200) {
        alert(`导入成功，共导入 ${response.data.data} 个书源`);
        setShowImportUrlDialog(false);
        setImportUrl('');
        refetch();
      } else {
        alert(response.data.message || '导入失败');
      }
    } catch (error) {
      alert('导入失败');
    }
  };

  const handleExportSource = async (sourceId: number) => {
    try {
      const response = await api.get<ApiResponse<BookSource>>(`/api/v1/sources/${sourceId}`);
      const blob = new Blob([JSON.stringify(response.data, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `source_${sourceId}.json`;
      a.click();
      URL.revokeObjectURL(url);
    } catch {
      alert('导出失败');
    }
  };

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
        <h1 className="text-3xl font-bold">书源管理</h1>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={handleImportSource}>
            <Download className="h-4 w-4 mr-2" />
            导入
          </Button>
          <Button variant="outline" size="sm" onClick={() => setShowImportUrlDialog(true)}>
            <LinkIcon className="h-4 w-4 mr-2" />
            URL导入
          </Button>
          <Button variant="outline" size="sm" onClick={() => refetch()}>
            <RefreshCw className="h-4 w-4 mr-2" />
            刷新
          </Button>
          <Link href="/sources/new">
            <Button size="sm">
              <Plus className="h-4 w-4 mr-2" />
              添加书源
            </Button>
          </Link>
        </div>
      </div>

      <div className="flex gap-4 mb-6">
        <div className="flex-1">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="搜索书源..."
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>
        <select
          value={selectedGroup}
          onChange={(e) => setSelectedGroup(e.target.value)}
          className="px-4 py-2 border rounded-md bg-background"
        >
          <option value="all">全部</option>
          {groups.map((group) => (
            <option key={group} value={group}>
              {group}
            </option>
          ))}
        </select>
      </div>

      <div className="space-y-4">
        {filteredSources.length === 0 ? (
          <Card>
            <CardContent className="text-center py-16">
              <p className="text-muted-foreground">暂无书源，请添加或导入书源</p>
            </CardContent>
          </Card>
        ) : (
          filteredSources.map((source) => (
            <Card key={source.id} className="hover:shadow-md transition-shadow">
              <CardContent className="p-4">
                <div className="flex items-start justify-between">
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-semibold truncate">{source.sourceName}</h3>
                      <Badge variant={source.enabled ? 'default' : 'secondary'}>
                        {source.enabled ? '启用' : '禁用'}
                      </Badge>
                      {source.sourceGroup && (
                        <Badge variant="outline">{source.sourceGroup}</Badge>
                      )}
                    </div>
                    <p className="text-sm text-muted-foreground truncate">{source.sourceUrl}</p>
                    <div className="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
                      <span>权重: {source.weight}</span>
                      {source.lastUpdateTime && (
                        <span>更新: {new Date(source.lastUpdateTime).toLocaleDateString()}</span>
                      )}
                      {source.respondTime && <span>响应: {source.respondTime}ms</span>}
                    </div>
                  </div>

                  <div className="flex items-center gap-1 ml-4">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleTestSource(source.id)}
                      title="测试"
                    >
                      <Play className="h-4 w-4" />
                    </Button>
                    <Link href={`/sources/${source.id}/edit`}>
                      <Button variant="ghost" size="icon" title="编辑">
                        <Pencil className="h-4 w-4" />
                      </Button>
                    </Link>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleExportSource(source.id)}
                      title="导出"
                    >
                      <Upload className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => handleDeleteSource(source.id)}
                      title="删除"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))
        )}
      </div>

      {showImportUrlDialog && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-background p-6 rounded-lg w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">从URL导入书源</h2>
            <Input
              placeholder="请输入书源JSON的URL地址"
              value={importUrl}
              onChange={(e) => setImportUrl(e.target.value)}
              className="mb-4"
            />
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setShowImportUrlDialog(false)}>
                取消
              </Button>
              <Button onClick={handleImportFromUrl}>导入</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
