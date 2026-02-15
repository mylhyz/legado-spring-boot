'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import { useAuthStore } from '@/stores/authStore';
import { BookOpen, Search, Settings, LogOut, User } from 'lucide-react';

const navItems = [
  { href: '/bookshelf', label: '书架', icon: BookOpen },
  { href: '/search', label: '搜索', icon: Search },
  { href: '/sources', label: '书源', icon: BookOpen },
  { href: '/settings', label: '设置', icon: Settings },
];

export function Header() {
  const pathname = usePathname();
  const { isAuthenticated, logout, user } = useAuthStore();

  return (
    <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 sticky top-0 z-50">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <div className="flex items-center gap-6">
          <Link href="/" className="flex items-center gap-2">
            <span className="text-xl font-bold">Legado</span>
          </Link>
          
          <nav className="hidden md:flex items-center gap-1">
            {navItems.map((item) => (
              <Link key={item.href} href={item.href}>
                <Button
                  variant={pathname === item.href ? 'secondary' : 'ghost'}
                  size="sm"
                  className={cn('gap-2', pathname === item.href && 'bg-accent')}
                >
                  <item.icon className="h-4 w-4" />
                  {item.label}
                </Button>
              </Link>
            ))}
          </nav>
        </div>

        <div className="flex items-center gap-2">
          {isAuthenticated ? (
            <>
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <User className="h-4 w-4" />
                <span>{user?.username}</span>
              </div>
              <Button
                variant="ghost"
                size="sm"
                onClick={logout}
              >
                <LogOut className="h-4 w-4" />
              </Button>
            </>
          ) : (
            <Link href="/login">
              <Button size="sm">登录</Button>
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}
