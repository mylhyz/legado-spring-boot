## Why

开源阅读 (Legado) 项目需要构建完整的 Web 端服务，以实现跨平台阅读体验。原版 Legado 仅支持 Android 客户端，用户无法在浏览器中阅读和管理书籍。通过改造和新建 legado-web 前端和 legado-server 后端服务，提供完整的 Web 端阅读解决方案，支持书架管理、全网搜索、在线阅读和书源管理等核心功能。

## What Changes

### legado-web (新建前端项目)
- 基于 React 18 + Next.js 14 构建现代化 Web 阅读应用
- 实现用户认证系统（登录/注册/JWT Token）
- 书架管理功能（书籍展示、阅读进度、删除、刷新）
- 全网搜索功能（多源并行搜索、结果展示、一键添加）
- 在线阅读器（章节导航、目录、主题切换、字体设置）
- 书源管理（CRUD、测试、导入导出）
- 系统设置（个人资料、阅读配置、外观设置）

### legado-server (新建后端服务)
- 基于 Spring Boot 2.7.18 + SQLite 构建 RESTful API
- 模块化架构（legado-api/legado-core/legado-model/legado-common）
- JWT 认证和 Spring Security 安全配置
- 书源规则引擎（基于 GraalVM JavaScript 解析）
- 并行搜索服务（多书源并发）
- Caffeine 本地缓存优化
- WebSocket 支持（阅读进度实时同步）
- Swagger API 文档

## Capabilities

### New Capabilities

- **web-auth**: 用户认证系统，支持 JWT Token 的注册、登录、Token 刷新
- **web-bookshelf**: 书架管理，包括书籍列表、详情、添加、删除、阅读进度同步
- **web-search**: 全网搜索，基于书源的多站点并行搜索
- **web-reader**: 在线阅读，章节内容获取、目录导航、阅读进度保存
- **web-source-management**: 书源管理，书源的 CRUD、测试、导入导出
- **server-booksource-engine**: 书源规则解析引擎，支持搜索、书籍信息、目录、正文规则
- **server-websocket**: WebSocket 实时通信，支持阅读进度多端同步

### Modified Capabilities

- 无（新建项目，不涉及现有能力修改）

## Impact

### 代码结构

- `legado-web/`: Next.js 14 前端项目，TypeScript + Tailwind CSS + Zustand
- `legado-server/`: Spring Boot 多模块 Maven 项目
  - `legado-api/`: API 接口层（Controller、Config、WebSocket）
  - `legado-core/`: 核心业务层（Service、书源引擎）
  - `legado-model/`: 数据模型层（Entity、Repository）
  - `legado-common/`: 公共工具层

### API 接口

- 认证接口: `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/me`
- 书籍接口: `/api/v1/books`, `/api/v1/books/{id}`, `/api/v1/books/search`
- 书源接口: `/api/v1/sources`, `/api/v1/sources/{id}`, `/api/v1/sources/{id}/test`

### 依赖项

- 前端: Next.js 14, React 18, TypeScript 5, Tailwind CSS 3, Zustand, TanStack Query, shadcn/ui
- 后端: Spring Boot 2.7.18, Spring Security, Spring Data JPA, SQLite, Caffeine, GraalVM JS, OkHttp, JWT

### 部署

- 前端: Node.js 18+, pnpm/npm, 输出静态文件
- 后端: JDK 1.8+, Maven, 打包为 JAR 运行
- 数据库: SQLite 单文件（`data/legado.db`）
