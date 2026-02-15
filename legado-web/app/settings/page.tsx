'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/stores/authStore';
import { useReaderStore } from '@/stores/readerStore';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Switch } from '@/components/ui/switch';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import {
  User,
  BookOpen,
  Palette,
  Bell,
  Shield,
  Trash2,
  LogOut,
  Save,
  Moon,
  Sun,
} from 'lucide-react';

export default function SettingsPage() {
  const router = useRouter();
  const { user, logout } = useAuthStore();
  const { settings, updateSettings } = useReaderStore();

  const [activeTab, setActiveTab] = useState('profile');

  const handleLogout = () => {
    logout();
    router.push('/login');
  };

  const tabs = [
    { id: 'profile', label: '个人资料', icon: User },
    { id: 'reader', label: '阅读设置', icon: BookOpen },
    { id: 'appearance', label: '外观', icon: Palette },
    { id: 'notification', label: '通知', icon: Bell },
    { id: 'security', label: '安全', icon: Shield },
  ];

  return (
    <div className="container mx-auto py-8 max-w-4xl">
      <h1 className="text-3xl font-bold mb-6">设置</h1>

      <div className="flex flex-col md:flex-row gap-6">
        {/* 侧边栏 */}
        <aside className="w-full md:w-64 space-y-2">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg text-left transition-colors ${
                activeTab === tab.id
                  ? 'bg-primary text-primary-foreground'
                  : 'hover:bg-muted'
              }`}
            >
              <tab.icon className="h-5 w-5" />
              {tab.label}
            </button>
          ))}

          <div className="pt-4 mt-4 border-t">
            <button
              onClick={handleLogout}
              className="w-full flex items-center gap-3 px-4 py-3 rounded-lg text-left text-destructive hover:bg-destructive/10 transition-colors"
            >
              <LogOut className="h-5 w-5" />
              退出登录
            </button>
          </div>
        </aside>

        {/* 内容区域 */}
        <div className="flex-1">
          {activeTab === 'profile' && (
            <Card>
              <CardHeader>
                <CardTitle>个人资料</CardTitle>
                <CardDescription>管理你的账户信息</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="flex items-center gap-4">
                  <div className="h-20 w-20 rounded-full bg-muted flex items-center justify-center">
                    <User className="h-10 w-10 text-muted-foreground" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-lg">{user?.username || '用户'}</h3>
                    <p className="text-sm text-muted-foreground">{user?.email || '未设置邮箱'}</p>
                  </div>
                </div>

                <div className="grid gap-4 md:grid-cols-2">
                  <div className="space-y-2">
                    <Label htmlFor="username">用户名</Label>
                    <Input id="username" defaultValue={user?.username || ''} disabled />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="email">邮箱</Label>
                    <Input id="email" type="email" defaultValue={user?.email || ''} placeholder="your@email.com" />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="nickname">昵称</Label>
                    <Input id="nickname" defaultValue={user?.nickname || ''} placeholder="你的昵称" />
                  </div>
                </div>

                <div className="flex justify-end">
                  <Button>
                    <Save className="h-4 w-4 mr-2" />
                    保存修改
                  </Button>
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === 'reader' && (
            <Card>
              <CardHeader>
                <CardTitle>阅读设置</CardTitle>
                <CardDescription>自定义你的阅读体验</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <Label>字体大小</Label>
                      <p className="text-sm text-muted-foreground">当前: {settings.fontSize}px</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => updateSettings({ fontSize: Math.max(12, settings.fontSize - 2) })}
                      >
                        A-
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => updateSettings({ fontSize: Math.min(32, settings.fontSize + 2) })}
                      >
                        A+
                      </Button>
                    </div>
                  </div>

                  <div className="flex items-center justify-between">
                    <div>
                      <Label>行间距</Label>
                      <p className="text-sm text-muted-foreground">当前: {settings.lineHeight}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => updateSettings({ lineHeight: Math.max(1.2, settings.lineHeight - 0.2) })}
                      >
                        -
                      </Button>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => updateSettings({ lineHeight: Math.min(2.5, settings.lineHeight + 0.2) })}
                      >
                        +
                      </Button>
                    </div>
                  </div>

                  <div className="flex items-center justify-between">
                    <div>
                      <Label>默认阅读模式</Label>
                      <p className="text-sm text-muted-foreground">选择翻页或滚动模式</p>
                    </div>
                    <Select
                      value={settings.pageMode}
                      onValueChange={(value: 'scroll' | 'pagination') => updateSettings({ pageMode: value })}
                    >
                      <SelectTrigger className="w-32">
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="scroll">滚动</SelectItem>
                        <SelectItem value="pagination">翻页</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === 'appearance' && (
            <Card>
              <CardHeader>
                <CardTitle>外观</CardTitle>
                <CardDescription>定制界面主题和样式</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <Label>默认主题</Label>
                    <p className="text-sm text-muted-foreground">选择阅读器默认主题</p>
                  </div>
                  <Select
                    value={settings.theme}
                    onValueChange={(value: 'light' | 'dark' | 'sepia') => updateSettings({ theme: value })}
                  >
                    <SelectTrigger className="w-32">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="light">
                        <div className="flex items-center gap-2">
                          <Sun className="h-4 w-4" />
                          白天
                        </div>
                      </SelectItem>
                      <SelectItem value="dark">
                        <div className="flex items-center gap-2">
                          <Moon className="h-4 w-4" />
                          夜间
                        </div>
                      </SelectItem>
                      <SelectItem value="sepia">护眼</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <Label>字体</Label>
                    <p className="text-sm text-muted-foreground">选择阅读字体</p>
                  </div>
                  <Select
                    value={settings.fontFamily}
                    onValueChange={(value) => updateSettings({ fontFamily: value })}
                  >
                    <SelectTrigger className="w-40">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="system-ui, sans-serif">系统字体</SelectItem>
                      <SelectItem value="'Songti SC', serif">宋体</SelectItem>
                      <SelectItem value="'Kaiti SC', STKaiti, serif">楷体</SelectItem>
                      <SelectItem value="'PingFang SC', sans-serif">苹方</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === 'notification' && (
            <Card>
              <CardHeader>
                <CardTitle>通知设置</CardTitle>
                <CardDescription>管理应用通知偏好</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="flex items-center justify-between">
                  <div>
                    <Label>新章节推送</Label>
                    <p className="text-sm text-muted-foreground">当订阅的书籍更新时通知</p>
                  </div>
                  <Switch defaultChecked />
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <Label>更新提醒</Label>
                    <p className="text-sm text-muted-foreground">书籍更新时提醒</p>
                  </div>
                  <Switch defaultChecked />
                </div>

                <div className="flex items-center justify-between">
                  <div>
                    <Label>阅读进度同步</Label>
                    <p className="text-sm text-muted-foreground">自动同步阅读进度</p>
                  </div>
                  <Switch defaultChecked />
                </div>
              </CardContent>
            </Card>
          )}

          {activeTab === 'security' && (
            <Card>
              <CardHeader>
                <CardTitle>安全设置</CardTitle>
                <CardDescription>保护你的账户安全</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                <div className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="current-password">当前密码</Label>
                    <Input id="current-password" type="password" placeholder="输入当前密码" />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="new-password">新密码</Label>
                    <Input id="new-password" type="password" placeholder="输入新密码" />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="confirm-password">确认新密码</Label>
                    <Input id="confirm-password" type="password" placeholder="再次输入新密码" />
                  </div>
                  <Button>修改密码</Button>
                </div>

                <div className="pt-6 border-t">
                  <h3 className="font-semibold mb-4">危险区域</h3>
                  <div className="flex items-center justify-between p-4 border rounded-lg bg-destructive/5">
                    <div>
                      <Label>删除账户</Label>
                      <p className="text-sm text-muted-foreground">永久删除你的账户和所有数据</p>
                    </div>
                    <Button variant="destructive" size="sm">
                      <Trash2 className="h-4 w-4 mr-2" />
                      删除
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}
