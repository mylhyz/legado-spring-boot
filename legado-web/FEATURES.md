# Legado Web 前端功能说明

## 1. 项目概述

Legado Web 是一个基于 React 18 + Next.js 14 的浏览器端在线阅读程序，为用户提供现代化的阅读体验。

### 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Next.js | 14.x | React 全栈框架 |
| React | 18.x | UI 框架 |
| TypeScript | 5.x | 类型安全 |
| Tailwind CSS | 3.x | 样式框架 |
| Zustand | 4.x | 状态管理 |
| TanStack Query | 5.x | 服务端状态管理 |
| shadcn/ui | - | UI 组件库 |
| Axios | - | HTTP 客户端 |
| Lucide React | - | 图标库 |

---

## 2. 核心功能

### 2.1 用户认证

- **登录/注册**：支持用户名、密码登录，邮箱注册
- **JWT 认证**：基于 Token 的无状态认证
- **自动登录**：Token 持久化存储

### 2.2 书架管理

- **书籍展示**：卡片式布局，支持封面显示
- **阅读进度**：显示已读章节数和总章节数
- **刷新功能**：一键刷新书籍列表
- **删除书籍**：支持删除本地书籍
- **跳转阅读**：点击书籍卡片进入阅读器

### 2.3 全网搜索

- **多源搜索**：基于书源的多站点并行搜索
- **结果展示**：显示书名、作者、来源、最新章节
- **书籍详情**：展示书籍简介和封面
- **一键添加**：快速将搜索结果添加到书架

### 2.4 在线阅读

- **章节导航**：上一章/下一章切换
- **目录侧边栏**：显示完整章节目录
- **阅读进度**：自动保存阅读位置
- **主题切换**：白天/夜间/护眼三种主题
- **字体设置**：字体大小、行间距可调
- **阅读模式**：支持滚动模式

### 2.5 书源管理

- **书源列表**：展示所有书源及状态
- **分组筛选**：按书源分组筛选
- **搜索功能**：快速定位书源
- **添加书源**：手动添加新书源
- **编辑书源**：修改书源配置
- **测试书源**：验证书源可用性
- **导入/导出**：支持书源 JSON 导入导出

### 2.6 系统设置

- **个人资料**：查看和修改用户信息
- **阅读设置**：默认字体大小、行间距、阅读模式
- **外观设置**：默认主题、阅读字体
- **通知设置**：新章节推送、更新提醒开关
- **安全设置**：修改密码、账户删除
- **退出登录**：安全退出当前账户

---

## 3. 页面结构

```
/                   # 首页 -> 书架
/login              # 登录/注册
/bookshelf          # 书架
/search             # 全网搜索
/reader/:bookId     # 阅读器
/sources            # 书源管理
/sources/new        # 添加书源
/sources/:id/edit   # 编辑书源
/settings           # 系统设置
```

---

## 4. 组件说明

### 4.1 通用组件

| 组件 | 说明 |
|------|------|
| Header | 顶部导航栏，包含导航链接和用户信息 |
| Button | 按钮组件，支持多种变体和尺寸 |
| Input | 输入框组件 |
| Card | 卡片容器 |
| Select | 下拉选择器 |
| Switch | 开关组件 |
| Tabs | 标签页组件 |
| Badge | 标签组件 |
| Textarea | 文本域组件 |
| Label | 标签组件 |

### 4.2 业务组件

| 组件 | 说明 |
|------|------|
| BookCard | 书籍卡片，展示书籍封面和信息 |
| ChapterList | 章节目录列表 |
| ReaderSettings | 阅读器设置面板 |

---

## 5. 状态管理

### 5.1 认证状态 (authStore)

```typescript
interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  setAuth: (token: string, user: User) => void;
  logout: () => void;
}
```

### 5.2 阅读器状态 (readerStore)

```typescript
interface ReaderState {
  currentBookId: number | null;
  currentChapter: number;
  scrollPosition: number;
  settings: {
    fontSize: number;
    lineHeight: number;
    fontFamily: string;
    theme: 'light' | 'dark' | 'sepia';
    pageMode: 'scroll' | 'pagination';
  };
  setCurrentChapter: (chapter: number) => void;
  updateSettings: (settings: Partial<ReaderSettings>) => void;
}
```

---

## 6. API 接口

### 6.1 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/auth/login | 用户登录 |
| POST | /api/v1/auth/register | 用户注册 |

### 6.2 书籍接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/books | 获取书架书籍列表 |
| GET | /api/v1/books/:id | 获取书籍详情 |
| GET | /api/v1/books/:id/chapters | 获取章节列表 |
| GET | /api/v1/books/:id/chapters/:index/content | 获取章节内容 |
| PUT | /api/v1/books/:id/progress | 保存阅读进度 |
| DELETE | /api/v1/books/:id | 删除书籍 |
| GET | /api/v1/books/search | 全网搜索 |
| POST | /api/v1/books/from-source | 从书源添加书籍 |

### 6.3 书源接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/v1/sources | 获取书源列表 |
| GET | /api/v1/sources/:id | 获取书源详情 |
| POST | /api/v1/sources | 创建书源 |
| PUT | /api/v1/sources/:id | 更新书源 |
| DELETE | /api/v1/sources/:id | 删除书源 |
| POST | /api/v1/sources/:id/test | 测试书源 |

---

## 7. 数据类型

### 7.1 Book (书籍)

```typescript
interface Book {
  id: number;
  name: string;
  author?: string;
  coverUrl?: string;
  intro?: string;
  totalChapterNum: number;
  durChapterIndex: number;
  durChapterTitle?: string;
  latestChapterTitle?: string;
  originName?: string;
}
```

### 7.2 BookChapter (章节)

```typescript
interface BookChapter {
  id: number;
  bookId: number;
  chapterIndex: number;
  title?: string;
  url?: string;
  content?: string;
  wordCount?: number;
  isVip: boolean;
  isPay: boolean;
}
```

### 7.3 BookSource (书源)

```typescript
interface BookSource {
  id: number;
  sourceName: string;
  sourceUrl: string;
  sourceGroup?: string;
  enabled: boolean;
  weight: number;
  searchUrl?: string;
  ruleSearch?: string;
  ruleBookInfo?: string;
  ruleToc?: string;
  ruleContent?: string;
}
```

---

## 8. 开发指南

### 8.1 安装依赖

```bash
cd legado-web
pnpm install
```

### 8.2 启动开发服务器

```bash
pnpm dev
```

访问 http://localhost:3000

### 8.3 构建生产版本

```bash
pnpm build
pnpm start
```

### 8.4 代码检查

```bash
pnpm lint
```

---

## 9. 功能特性

### 9.1 阅读体验

- [x] 三种阅读主题（白天/夜间/护眼）
- [x] 字体大小调节（12-32px）
- [x] 行间距调节（1.2-2.5）
- [x] 多种阅读字体选择
- [x] 自动保存阅读进度
- [x] 章节跳转功能
- [x] 目录快速导航

### 9.2 数据管理

- [x] 书籍本地缓存
- [x] 阅读设置持久化
- [x] Token 自动刷新
- [x] 请求错误重试

### 9.3 用户交互

- [x] 响应式布局
- [x] 加载状态提示
- [x] 操作反馈提示
- [x] 表单验证

---

## 10. 后续开发计划

- [ ] 书籍分组管理
- [ ] 书籍排序功能
- [ ] 书签功能
- [ ] 阅读统计
- [ ] 夜间模式自动切换
- [ ] PWA 离线支持
- [ ] 虚拟滚动优化
- [ ] WebSocket 实时推送
