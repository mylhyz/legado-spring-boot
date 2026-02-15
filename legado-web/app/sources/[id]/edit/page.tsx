'use client';

import { useState, useEffect } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { useQuery, useMutation } from '@tanstack/react-query';
import { api } from '@/lib/api';
import { BookSource, ApiResponse } from '@/types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ArrowLeft, Save, Play, Loader2 } from 'lucide-react';

export default function EditSourcePage() {
  const router = useRouter();
  const params = useParams();
  const sourceId = params.id === 'new' ? null : Number(params.id);
  const isNew = sourceId === null;

  const [formData, setFormData] = useState<Partial<BookSource>>({
    sourceName: '',
    sourceUrl: '',
    sourceGroup: '',
    enabled: true,
    enabledExplore: true,
    weight: 0,
    customOrder: 0,
    loginUrl: '',
    header: '',
    searchUrl: '',
    exploreUrl: '',
    ruleSearch: '',
    ruleBookInfo: '',
    ruleToc: '',
    ruleContent: '',
  });

  const { data: sourceData, isLoading } = useQuery<ApiResponse<BookSource>>({
    queryKey: ['source', sourceId],
    queryFn: () => api.get(`/api/v1/sources/${sourceId}`),
    enabled: !isNew && !!sourceId,
  });

  useEffect(() => {
    if (sourceData?.data) {
      setFormData(sourceData.data);
    }
  }, [sourceData]);

  const saveMutation = useMutation({
    mutationFn: async (data: Partial<BookSource>) => {
      if (isNew) {
        return api.post<ApiResponse<BookSource>>('/api/v1/sources', data);
      } else {
        return api.put<ApiResponse<BookSource>>(`/api/v1/sources/${sourceId}`, data);
      }
    },
    onSuccess: () => {
      alert(isNew ? '创建成功' : '保存成功');
      router.push('/sources');
    },
    onError: () => {
      alert('保存失败');
    },
  });

  const testMutation = useMutation({
    mutationFn: async () => {
      return api.post<ApiResponse<boolean>>(`/api/v1/sources/${sourceId}/test`);
    },
    onSuccess: (res) => {
      if (res.data) {
        alert('测试成功');
      } else {
        alert('测试失败');
      }
    },
    onError: () => {
      alert('测试失败');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    saveMutation.mutate(formData);
  };

  if (!isNew && isLoading) {
    return (
      <div className="container mx-auto py-8">
        <div className="flex items-center justify-center h-64">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 max-w-4xl">
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" size="icon" onClick={() => router.back()}>
          <ArrowLeft className="h-5 w-5" />
        </Button>
        <h1 className="text-3xl font-bold">{isNew ? '添加书源' : '编辑书源'}</h1>
      </div>

      <form onSubmit={handleSubmit}>
        <Tabs defaultValue="basic" className="space-y-6">
          <TabsList>
            <TabsTrigger value="basic">基本设置</TabsTrigger>
            <TabsTrigger value="search">搜索规则</TabsTrigger>
            <TabsTrigger value="toc">目录规则</TabsTrigger>
            <TabsTrigger value="content">正文规则</TabsTrigger>
            <TabsTrigger value="advanced">高级设置</TabsTrigger>
          </TabsList>

          <TabsContent value="basic">
            <Card>
              <CardHeader>
                <CardTitle>基本信息</CardTitle>
                <CardDescription>书源的基本配置</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="sourceName">书源名称</Label>
                    <Input
                      id="sourceName"
                      value={formData.sourceName || ''}
                      onChange={(e) => setFormData({ ...formData, sourceName: e.target.value })}
                      placeholder="例如：笔趣阁"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="sourceUrl">书源URL</Label>
                    <Input
                      id="sourceUrl"
                      value={formData.sourceUrl || ''}
                      onChange={(e) => setFormData({ ...formData, sourceUrl: e.target.value })}
                      placeholder="https://example.com"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="sourceGroup">分组</Label>
                    <Input
                      id="sourceGroup"
                      value={formData.sourceGroup || ''}
                      onChange={(e) => setFormData({ ...formData, sourceGroup: e.target.value })}
                      placeholder="例如：小说"
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="weight">权重</Label>
                    <Input
                      id="weight"
                      type="number"
                      value={formData.weight || 0}
                      onChange={(e) => setFormData({ ...formData, weight: Number(e.target.value) })}
                    />
                  </div>
                </div>

                <div className="flex items-center gap-6">
                  <div className="flex items-center gap-2">
                    <Switch
                      id="enabled"
                      checked={formData.enabled}
                      onCheckedChange={(checked) => setFormData({ ...formData, enabled: checked })}
                    />
                    <Label htmlFor="enabled">启用</Label>
                  </div>
                  <div className="flex items-center gap-2">
                    <Switch
                      id="enabledExplore"
                      checked={formData.enabledExplore}
                      onCheckedChange={(checked) => setFormData({ ...formData, enabledExplore: checked })}
                    />
                    <Label htmlFor="enabledExplore">启用探索</Label>
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="search">
            <Card>
              <CardHeader>
                <CardTitle>搜索规则</CardTitle>
                <CardDescription>配置搜索功能</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="searchUrl">搜索URL</Label>
                  <Input
                    id="searchUrl"
                    value={formData.searchUrl || ''}
                    onChange={(e) => setFormData({ ...formData, searchUrl: e.target.value })}
                    placeholder="https://example.com/search?wd={{key}}"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="ruleSearch">搜索结果规则</Label>
                  <Textarea
                    id="ruleSearch"
                    value={formData.ruleSearch || ''}
                    onChange={(e) => setFormData({ ...formData, ruleSearch: e.target.value })}
                    placeholder="JSON格式的搜索规则"
                    rows={6}
                  />
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="toc">
            <Card>
              <CardHeader>
                <CardTitle>目录规则</CardTitle>
                <CardDescription>配置目录获取规则</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="ruleToc">目录规则</Label>
                  <Textarea
                    id="ruleToc"
                    value={formData.ruleToc || ''}
                    onChange={(e) => setFormData({ ...formData, ruleToc: e.target.value })}
                    placeholder="JSON格式的目录规则"
                    rows={6}
                  />
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="content">
            <Card>
              <CardHeader>
                <CardTitle>正文规则</CardTitle>
                <CardDescription>配置正文获取规则</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="ruleContent">正文规则</Label>
                  <Textarea
                    id="ruleContent"
                    value={formData.ruleContent || ''}
                    onChange={(e) => setFormData({ ...formData, ruleContent: e.target.value })}
                    placeholder="JSON格式的正文规则"
                    rows={6}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="ruleBookInfo">书籍信息规则</Label>
                  <Textarea
                    id="ruleBookInfo"
                    value={formData.ruleBookInfo || ''}
                    onChange={(e) => setFormData({ ...formData, ruleBookInfo: e.target.value })}
                    placeholder="JSON格式的书籍信息规则"
                    rows={6}
                  />
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="advanced">
            <Card>
              <CardHeader>
                <CardTitle>高级设置</CardTitle>
                <CardDescription>其他高级配置</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="header">请求头</Label>
                  <Textarea
                    id="header"
                    value={formData.header || ''}
                    onChange={(e) => setFormData({ ...formData, header: e.target.value })}
                    placeholder="User-Agent: Mozilla/5.0"
                    rows={4}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="loginUrl">登录URL</Label>
                  <Input
                    id="loginUrl"
                    value={formData.loginUrl || ''}
                    onChange={(e) => setFormData({ ...formData, loginUrl: e.target.value })}
                    placeholder="登录页面URL"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="bookUrlPattern">书籍URL正则</Label>
                  <Input
                    id="bookUrlPattern"
                    value={formData.bookUrlPattern || ''}
                    onChange={(e) => setFormData({ ...formData, bookUrlPattern: e.target.value })}
                    placeholder=".*"
                  />
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>

        <div className="flex justify-end gap-4 mt-6">
          {!isNew && (
            <Button
              type="button"
              variant="outline"
              onClick={() => testMutation.mutate()}
              disabled={testMutation.isPending}
            >
              {testMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              <Play className="mr-2 h-4 w-4" />
              测试
            </Button>
          )}
          <Button type="submit" disabled={saveMutation.isPending}>
            {saveMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            <Save className="mr-2 h-4 w-4" />
            保存
          </Button>
        </div>
      </form>
    </div>
  );
}
