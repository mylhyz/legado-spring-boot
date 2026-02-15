'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { api } from '@/lib/api';
import { useAuthStore } from '@/stores/authStore';
import { LoginRequest, RegisterRequest, ApiResponse, LoginResponse } from '@/types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Loader2 } from 'lucide-react';

export default function LoginPage() {
  const router = useRouter();
  const { setAuth } = useAuthStore();
  
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (isLogin) {
        const loginData: LoginRequest = { username, password };
        const response = await api.post<ApiResponse<LoginResponse>>('/api/v1/auth/login', loginData);
        
        if (response.code === 200) {
          setAuth(response.data.token, response.data.user);
          router.push('/bookshelf');
        } else {
          setError(response.message || '登录失败');
        }
      } else {
        const registerData: RegisterRequest = { username, password, email };
        const response = await api.post<ApiResponse<LoginResponse>>('/api/v1/auth/register', registerData);
        
        if (response.code === 200) {
          setAuth(response.data.token, response.data.user);
          router.push('/bookshelf');
        } else {
          setError(response.message || '注册失败');
        }
      }
    } catch (err: any) {
      setError(err.response?.data?.message || '请求失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-b from-background to-muted p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">Legado</CardTitle>
          <CardDescription>
            {isLogin ? '登录到你的账户' : '创建一个新账户'}
          </CardDescription>
        </CardHeader>
        
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            {!isLogin && (
              <div className="space-y-2">
                <label className="text-sm font-medium">邮箱（可选）</label>
                <Input
                  type="email"
                  placeholder="your@email.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            )}
            
            <div className="space-y-2">
              <label className="text-sm font-medium">用户名</label>
              <Input
                type="text"
                placeholder="用户名"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            
            <div className="space-y-2">
              <label className="text-sm font-medium">密码</label>
              <Input
                type="password"
                placeholder="密码"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                minLength={6}
              />
            </div>

            {error && (
              <p className="text-sm text-destructive text-center">{error}</p>
            )}

            <Button type="submit" className="w-full" disabled={loading}>
              {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              {isLogin ? '登录' : '注册'}
            </Button>
          </form>

          <div className="mt-4 text-center text-sm">
            {isLogin ? (
              <>
                还没有账户？{' '}
                <button
                  type="button"
                  className="text-primary hover:underline"
                  onClick={() => setIsLogin(false)}
                >
                  注册
                </button>
              </>
            ) : (
              <>
                已有账户？{' '}
                <button
                  type="button"
                  className="text-primary hover:underline"
                  onClick={() => setIsLogin(true)}
                >
                  登录
                </button>
              </>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
