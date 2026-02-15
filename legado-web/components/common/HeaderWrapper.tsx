'use client';

import { usePathname } from 'next/navigation';
import { Header } from './Header';

export function HeaderWrapper() {
  const pathname = usePathname();
  const showHeader = !pathname.startsWith('/login');
  
  return showHeader ? <Header /> : null;
}
