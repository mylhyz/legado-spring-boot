# Legado 前后端分离改造技术方案

## 1. 项目概述

### 1.1 当前架构
- **平台**: Android原生应用 + 嵌入式Web管理界面
- **后端**: Kotlin + Android Room (SQLite)
- **前端**: Vue 3 + Vite + Element Plus (现有Web模块)
- **书源引擎**: Rhino JavaScript引擎
- **Web服务**: NanoHTTPD嵌入式服务器

### 1.2 目标架构
- **后端**: JDK8 + Spring Boot 2.7.x
- **前端**: React 18 + TypeScript + Next.js 14 (App Router)
- **数据库**: SQLite (保持与原项目一致)
- **本地缓存**: Caffeine
- **书源引擎**: GraalVM JavaScript引擎 / 自定义规则引擎
- **通信**: RESTful API + WebSocket

---

## 2. 技术选型

### 2.1 后端技术栈

| 技术/框架 | 版本 | 说明 |
|-----------|------|------|
| JDK | 1.8 | Java开发环境 |
| Spring Boot | 2.7.x | 主框架 |
| Spring Data JPA | 2.7.x | ORM框架 |
| SQLite | 3.x | 嵌入式数据库 |
| SQLite JDBC | 3.45.x | JDBC驱动 |
| Hibernate Community Dialects | 6.x | SQLite方言支持 |
| GraalVM JS | 22.x | JavaScript引擎(替代Rhino) |
| Jsoup | 1.16.x | HTML解析 |
| JSON Path | 2.9.x | JSON解析 |
| OkHttp | 4.12.x | HTTP客户端 |
| Lombok | 1.18.x | 代码简化 |
| MapStruct | 1.5.x | 对象映射 |
| JWT | 0.11.x | 身份认证 |
| WebSocket | Spring内置 | 实时通信 |
| Swagger/OpenAPI | 3.x | API文档 |
| Caffeine | 3.x | 本地缓存 |

### 2.2 前端技术栈

| 技术/框架 | 版本 | 说明 |
|-----------|------|------|
| React | 18.x | 前端框架 |
| Next.js | 14.x | React全栈框架 (App Router) |
| TypeScript | 5.x | 类型系统 |
| Tailwind CSS | 3.x | 原子化CSS框架 |
| shadcn/ui | 最新 | 现代化UI组件库 |
| Radix UI | 最新 | 无样式UI组件原语 |
| Zustand | 4.x | 轻量级状态管理 |
| TanStack Query | 5.x | 服务端状态管理 |
| Axios | 1.6.x | HTTP客户端 |
| Framer Motion | 11.x | 动画库 |
| Lucide React | 最新 | 图标库 |

---

## 3. 系统架构设计

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                        客户端层                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Web浏览器   │  │   手机浏览器  │  │    APP       │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────────┬───────────────────────────────────┘
                          │ HTTP/WebSocket
┌─────────────────────────▼───────────────────────────────────┐
│                      网关/负载均衡层                         │
│                    (Nginx / Gateway)                         │
└─────────────────────────┬───────────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────────┐
│                      后端服务层 (Spring Boot)                │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                 API Gateway                         │   │
│  │            (统一认证/限流/路由)                      │   │
│  └──────────────┬──────────────────────┬───────────────┘   │
│                 │                      │                   │
│  ┌──────────────▼──────┐  ┌───────────▼────────────┐      │
│  │    REST API 模块     │  │    WebSocket 模块      │      │
│  │  (HTTP请求处理)      │  │  (实时推送/进度同步)   │      │
│  └──────────────┬──────┘  └────────────┬───────────┘      │
│                 │                      │                   │
│  ┌──────────────▼──────────────────────▼───────────────┐   │
│  │                  业务服务层                         │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │   │
│  │  │ 书源服务 │ │ 书籍服务 │ │ 阅读服务 │ │ 用户服务 │  │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘  │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐  │   │
│  │  │ 缓存服务 │ │ 搜索服务 │ │ 任务调度 │ │ 存储服务 │  │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘  │   │
│  └────────────────────────────────────────────────────┘   │
│                           │                               │
│  ┌────────────────────────▼────────────────────────────┐  │
│  │                  数据访问层                         │  │
│  │         JPA Repository / Spring Data JPA           │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────┬────────────────────────────────┘
                           │
┌──────────────────────────▼────────────────────────────────┐
│                       数据存储层                           │
│  ┌──────────────────┐  ┌──────────────────┐              │
│  │      SQLite      │  │    Caffeine      │              │
│  │   (主数据库)      │  │   (本地缓存)      │              │
│  │   legado.db      │  │                  │              │
│  └──────────────────┘  └──────────────────┘              │
│  ┌──────────────────┐                                    │
│  │    文件存储       │                                    │
│  │ (书籍/图片/备份)  │                                    │
│  └──────────────────┘                                    │
└───────────────────────────────────────────────────────────┘
```

### 3.2 模块划分

```
legado-server/
├── legado-api/                    # API接口模块
│   ├── src/main/java/io/legado/api/
│   │   ├── controller/            # REST控制器
│   │   ├── dto/                   # 数据传输对象
│   │   └── websocket/             # WebSocket处理器
│   └── pom.xml
│
├── legado-core/                   # 核心业务模块
│   ├── src/main/java/io/legado/core/
│   │   ├── service/               # 业务服务
│   │   ├── booksource/            # 书源引擎
│   │   ├── cache/                 # 缓存管理
│   │   ├── crawler/               # 爬虫服务
│   │   └── storage/               # 存储服务
│   └── pom.xml
│
├── legado-model/                  # 数据模型模块
│   ├── src/main/java/io/legado/model/
│   │   ├── entity/                # 数据库实体
│   │   ├── repository/            # JPA仓库
│   │   └── enums/                 # 枚举类
│   └── pom.xml
│
├── legado-common/                 # 公共工具模块
│   ├── src/main/java/io/legado/common/
│   │   ├── config/                # 配置类
│   │   ├── exception/             # 异常处理
│   │   ├── utils/                 # 工具类
│   │   └── security/              # 安全相关
│   └── pom.xml
│
└── legado-web/                    # 前端项目
    ├── src/
    │   ├── api/                   # API封装
    │   ├── views/                 # 页面组件
    │   ├── components/            # 通用组件
    │   ├── store/                 # 状态管理
    │   └── router/                # 路由配置
    └── package.json
```

---

## 4. 数据库设计

### 4.1 数据库选型说明

**选择SQLite的理由：**
1. **轻量级**: 单文件存储，无需单独安装数据库服务
2. **零配置**: 开箱即用，适合个人/单机部署
3. **兼容性**: 与原Android项目数据库格式一致，便于数据迁移
4. **性能**: 对于单机阅读应用足够使用
5. **便携性**: 数据库文件可轻松备份和迁移

**SQLite配置：**
```yaml
spring:
  datasource:
    url: jdbc:sqlite:./data/legado.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.community.dialect.SQLiteDialect
```

### 4.2 实体类映射

将现有30个Kotlin实体类转换为JPA实体：

| 原实体类 | 说明 | 关键字段 |
|----------|------|----------|
| `Book` | 书籍信息 | id, name, author, bookUrl, coverUrl, intro |
| `BookChapter` | 章节信息 | id, bookId, index, title, url, content |
| `BookSource` | 书源配置 | id, sourceName, sourceUrl, ruleSearch, ruleBookInfo |
| `RssSource` | RSS订阅源 | id, sourceName, sourceUrl, ruleArticles |
| `BookGroup` | 书籍分组 | id, groupName, order, show |
| `ReplaceRule` | 替换规则 | id, name, pattern, replacement, isRegex |
| `Bookmark` | 书签 | id, bookId, chapter, position, content |
| `ReadRecord` | 阅读记录 | id, bookId, date, duration |
| `SearchBook` | 搜索结果 | id, name, author, bookUrl, origin |
| `BookProgress` | 阅读进度 | id, bookId, chapterIndex, charIndex |
| `Cache` | 缓存记录 | id, bookId, chapterIndex |
| `Cookie` | Cookie存储 | id, url, cookie |

### 4.3 数据库表结构

```sql
-- 书籍表
CREATE TABLE IF NOT EXISTS books (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_url TEXT NOT NULL UNIQUE,
    toc_url TEXT,
    origin TEXT NOT NULL DEFAULT 'local',
    origin_name TEXT,
    name TEXT NOT NULL,
    author TEXT,
    kind TEXT,
    custom_tag TEXT,
    cover_url TEXT,
    custom_cover_url TEXT,
    intro TEXT,
    custom_intro TEXT,
    charset TEXT,
    type INTEGER DEFAULT 0,
    group_id INTEGER,
    latest_chapter_title TEXT,
    latest_chapter_time INTEGER,
    last_check_time INTEGER,
    last_check_count INTEGER DEFAULT 0,
    total_chapter_num INTEGER DEFAULT 0,
    dur_chapter_title TEXT,
    dur_chapter_index INTEGER DEFAULT 0,
    dur_chapter_pos INTEGER DEFAULT 0,
    dur_chapter_time INTEGER,
    word_count TEXT,
    can_update INTEGER DEFAULT 1,
    order_num INTEGER DEFAULT 0,
    origin_order INTEGER DEFAULT 0,
    variable TEXT,
    read_config TEXT,  -- JSON格式存储
    sync_time INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_books_name_author ON books(name, author);
CREATE INDEX IF NOT EXISTS idx_books_origin ON books(origin);
CREATE INDEX IF NOT EXISTS idx_books_group_id ON books(group_id);

-- 章节表
CREATE TABLE IF NOT EXISTS book_chapters (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id INTEGER NOT NULL,
    chapter_index INTEGER NOT NULL,
    title TEXT,
    url TEXT,
    content TEXT,
    word_count INTEGER,
    is_vip INTEGER DEFAULT 0,
    is_pay INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(book_id, chapter_index)
);

CREATE INDEX IF NOT EXISTS idx_chapters_book_id ON book_chapters(book_id);

-- 书源表
CREATE TABLE IF NOT EXISTS book_sources (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_name TEXT NOT NULL,
    source_url TEXT NOT NULL,
    source_icon TEXT,
    source_group TEXT,
    enabled INTEGER DEFAULT 1,
    enabled_explore INTEGER DEFAULT 1,
    weight INTEGER DEFAULT 0,
    custom_order INTEGER DEFAULT 0,
    login_url TEXT,
    login_ui TEXT,
    login_check_js TEXT,
    book_url_pattern TEXT,
    header TEXT,
    search_url TEXT,
    explore_url TEXT,
    rule_search TEXT,  -- JSON格式
    rule_book_info TEXT,  -- JSON格式
    rule_toc TEXT,  -- JSON格式
    rule_content TEXT,  -- JSON格式
    rule_review TEXT,  -- JSON格式
    last_update_time INTEGER,
    respond_time INTEGER,
    content_replace_rule TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sources_name ON book_sources(source_name);
CREATE INDEX IF NOT EXISTS idx_sources_enabled ON book_sources(enabled);

-- 书源分组表
CREATE TABLE IF NOT EXISTS book_groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_name TEXT NOT NULL,
    group_icon TEXT,
    order_num INTEGER DEFAULT 0,
    show INTEGER DEFAULT 1
);

-- 替换规则表
CREATE TABLE IF NOT EXISTS replace_rules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    pattern TEXT NOT NULL,
    replacement TEXT,
    is_regex INTEGER DEFAULT 1,
    scope TEXT,
    is_enabled INTEGER DEFAULT 1,
    order_num INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 书签表
CREATE TABLE IF NOT EXISTS bookmarks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_name TEXT NOT NULL,
    book_author TEXT,
    book_url TEXT,
    chapter_name TEXT,
    chapter_index INTEGER,
    page_index INTEGER,
    content TEXT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 阅读记录表
CREATE TABLE IF NOT EXISTS read_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_name TEXT NOT NULL,
    book_author TEXT,
    read_date TEXT,
    read_time INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- RSS订阅源表
CREATE TABLE IF NOT EXISTS rss_sources (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_name TEXT NOT NULL,
    source_url TEXT NOT NULL,
    source_icon TEXT,
    enabled INTEGER DEFAULT 1,
    rule_order TEXT,
    header TEXT,
    article_style INTEGER DEFAULT 0,
    load_with_base_url INTEGER DEFAULT 0,
    rule_articles TEXT,  -- JSON格式
    rule_title TEXT,
    rule_pub_date TEXT,
    rule_description TEXT,
    rule_image TEXT,
    rule_link TEXT,
    last_update_time INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- RSS文章表
CREATE TABLE IF NOT EXISTS rss_articles (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    source_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    content TEXT,
    link TEXT,
    pub_date INTEGER,
    image TEXT,
    read INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rss_articles_source ON rss_articles(source_id);

-- Cookie存储表
CREATE TABLE IF NOT EXISTS cookies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    url TEXT NOT NULL UNIQUE,
    cookie TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4.4 JPA实体配置示例

```java
@Data
@Entity
@Table(name = "books", 
       indexes = {
           @Index(name = "idx_books_name_author", columnList = "name, author"),
           @Index(name = "idx_books_origin", columnList = "origin"),
           @Index(name = "idx_books_group_id", columnList = "group_id")
       })
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "book_url", nullable = false, unique = true, length = 2048)
    private String bookUrl;
    
    @Column(name = "toc_url", length = 2048)
    private String tocUrl;
    
    @Column(name = "origin", nullable = false, length = 256)
    private String origin = "local";
    
    @Column(name = "name", nullable = false, length = 256)
    private String name;
    
    @Column(name = "author", length = 256)
    private String author;
    
    @Column(name = "cover_url", length = 2048)
    private String coverUrl;
    
    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;
    
    @Column(name = "dur_chapter_index")
    private Integer durChapterIndex = 0;
    
    @Column(name = "dur_chapter_pos")
    private Integer durChapterPos = 0;
    
    @Column(name = "read_config", columnDefinition = "TEXT")
    @Convert(converter = ReadConfigConverter.class)
    private ReadConfig readConfig;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookChapter> chapters;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

// JSON转换器
@Converter
public class ReadConfigConverter implements AttributeConverter<ReadConfig, String> {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(ReadConfig attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Override
    public ReadConfig convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, ReadConfig.class);
        } catch (Exception e) {
            return new ReadConfig();
        }
    }
}
```

---

## 5. 书源规则引擎迁移

### 5.1 原方案分析

原Android端使用Rhino JavaScript引擎：
- **优点**: 可以直接执行JavaScript代码
- **缺点**: Rhino在JDK 8上性能较差，不支持ES6+

### 5.2 新方案选型

**推荐方案: GraalVM JavaScript引擎**

```java
// 书源规则执行引擎
@Service
public class BookSourceEngine {
    
    private final Context polyglot;
    
    public BookSourceEngine() {
        this.polyglot = Context.newBuilder("js")
            .allowHostAccess(HostAccess.ALL)
            .allowHostClassLookup(className -> true)
            .build();
    }
    
    public BookInfo parseBookInfo(String html, String ruleJson) {
        // 解析书源规则
        BookSourceRule rule = JSON.parseObject(ruleJson, BookSourceRule.class);
        
        // 注入JS上下文
        polyglot.getBindings("js").putMember("html", html);
        polyglot.getBindings("js").putMember("baseUrl", baseUrl);
        
        // 执行规则解析
        String script = buildRuleScript(rule);
        Value result = polyglot.eval("js", script);
        
        return convertToBookInfo(result);
    }
}
```

**备选方案: 自定义规则DSL**
- 如果JavaScript引擎性能不佳，可以开发简单的规则DSL
- 使用Java实现规则解析器
- 更轻量级，性能更好

### 5.3 规则数据结构

```java
@Data
public class BookSourceRule {
    private String bookName;
    private String author;
    private String intro;
    private String coverUrl;
    private String tocUrl;
    private String chapterList;
    private String chapterName;
    private String chapterUrl;
    private String content;
    private String nextContentUrl;
}
```

---

## 6. 核心功能实现方案

### 6.1 书籍搜索服务

```java
@Service
public class BookSearchService {
    
    @Async("taskExecutor")
    public CompletableFuture<List<SearchBook>> search(String keyword) {
        // 获取所有启用的书源
        List<BookSource> sources = bookSourceRepository.findByEnabledTrue();
        
        // 并行搜索
        List<CompletableFuture<List<SearchBook>>> futures = sources.stream()
            .map(source -> searchFromSource(source, keyword))
            .collect(Collectors.toList());
        
        // 合并结果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .flatMap(f -> f.join().stream())
                .collect(Collectors.toList()));
    }
    
    private CompletableFuture<List<SearchBook>> searchFromSource(
            BookSource source, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = buildSearchUrl(source.getSearchUrl(), keyword);
                String html = httpClient.get(url, source.getHeaders());
                return bookSourceEngine.parseSearchResult(html, source.getRuleSearch());
            } catch (Exception e) {
                log.error("搜索书源失败: {}", source.getSourceName(), e);
                return Collections.emptyList();
            }
        }, taskExecutor);
    }
}
```

### 6.2 章节内容抓取

```java
@Service
public class ChapterCrawlerService {
    
    @Cacheable(value = "chapterContent", key = "#bookId + ':' + #chapterIndex")
    public String fetchChapterContent(Long bookId, Integer chapterIndex) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        BookChapter chapter = chapterRepository.findByBookIdAndChapterIndex(bookId, chapterIndex);
        BookSource source = bookSourceRepository.findBySourceUrl(book.getOrigin());
        
        // 抓取内容
        String content = crawlContent(chapter.getUrl(), source);
        
        // 应用替换规则
        content = applyReplaceRules(content, book.getOrigin());
        
        return content;
    }
    
    private String crawlContent(String url, BookSource source) {
        StringBuilder content = new StringBuilder();
        String currentUrl = url;
        int maxPages = 10; // 防止死循环
        
        for (int i = 0; i < maxPages && StringUtils.isNotBlank(currentUrl); i++) {
            String html = httpClient.get(currentUrl, source.getHeaders());
            ContentResult result = bookSourceEngine.parseContent(html, source.getRuleContent());
            content.append(result.getContent());
            currentUrl = result.getNextUrl();
        }
        
        return content.toString();
    }
}
```

### 6.3 缓存策略

由于使用SQLite单机数据库，采用Caffeine本地缓存：

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofHours(1)));
        return cacheManager;
    }
}

@Service
public class CacheService {
    
    @Cacheable(value = "bookInfo", key = "#bookId")
    public Book getBookInfo(Long bookId) {
        return bookRepository.findById(bookId).orElse(null);
    }
    
    @CacheEvict(value = "bookInfo", key = "#bookId")
    public void updateBookInfo(Long bookId, Book book) {
        bookRepository.save(book);
    }
    
    @Cacheable(value = "chapterContent", key = "#bookId + ':' + #chapterIndex")
    public String getChapterContent(Long bookId, Integer chapterIndex) {
        BookChapter chapter = chapterRepository
            .findByBookIdAndChapterIndex(bookId, chapterIndex);
        return chapter != null ? chapter.getContent() : null;
    }
}
```

---

## 7. API接口设计

### 7.1 RESTful API规范

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户注册 | POST | /api/v1/auth/register | 用户注册 |
| 用户登录 | POST | /api/v1/auth/login | 用户登录 |
| 获取书籍列表 | GET | /api/v1/books | 分页获取书籍 |
| 获取书籍详情 | GET | /api/v1/books/{id} | 获取书籍详细信息 |
| 添加书籍 | POST | /api/v1/books | 添加书籍到书架 |
| 删除书籍 | DELETE | /api/v1/books/{id} | 从书架删除书籍 |
| 获取章节列表 | GET | /api/v1/books/{id}/chapters | 获取书籍章节列表 |
| 获取章节内容 | GET | /api/v1/books/{id}/chapters/{index}/content | 获取章节内容 |
| 搜索书籍 | GET | /api/v1/search | 搜索书籍 |
| 获取书源列表 | GET | /api/v1/sources | 获取书源列表 |
| 添加书源 | POST | /api/v1/sources | 添加新书源 |
| 测试书源 | POST | /api/v1/sources/{id}/test | 测试书源可用性 |
| 更新阅读进度 | PUT | /api/v1/books/{id}/progress | 更新阅读进度 |
| 获取阅读进度 | GET | /api/v1/books/{id}/progress | 获取阅读进度 |
| 导出数据 | GET | /api/v1/backup/export | 导出SQLite数据库 |
| 导入数据 | POST | /api/v1/backup/import | 导入SQLite数据库 |

### 7.2 WebSocket接口

```java
@ServerEndpoint("/ws/reader")
@Component
public class ReaderWebSocket {
    
    // 发送阅读进度同步
    public void syncProgress(Long bookId, ReadProgress progress) {
        String message = JSON.toJSONString(progress);
        sessions.forEach(session -> {
            if (isSubscribed(session, bookId)) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }
    
    // 发送章节缓存进度
    public void sendCacheProgress(Long bookId, int cachedCount, int totalCount) {
        CacheProgress progress = new CacheProgress(bookId, cachedCount, totalCount);
        broadcast("CACHE_PROGRESS", progress);
    }
}
```

---

## 8. 前端改造方案

### 8.1 目标定位

打造一个现代化的**浏览器端在线阅读程序**，核心目标：
- 📚 书架管理 - 浏览、分类、搜索已添加的书籍
- 📖 在线阅读 - 流畅的阅读体验，支持字体、主题、翻页设置
- ⚙️ 书源管理 - 导入、编辑、测试书源规则
- 🔍 全网搜索 - 基于书源的多站点并行搜索
- ☁️ 云端同步 - 阅读进度、书签云端保存（可选）

### 8.2 技术选型理由

| 技术 | 选择理由 |
|------|----------|
| **Next.js 14** | React全栈框架，支持SSR/SSG，App Router提供更好的路由体验 |
| **shadcn/ui** | 基于Radix UI的高质量组件库，可定制化强，与Tailwind完美配合 |
| **Zustand** | 轻量级状态管理，比Redux更简单，比Context性能更好 |
| **TanStack Query** | 服务端状态管理，缓存、重试、乐观更新一应俱全 |
| **Tailwind CSS** | 原子化CSS，开发效率高，文件体积小 |

### 8.3 改造内容

1. **框架迁移**: Vue 3 → React 18 + Next.js 14 (App Router)
2. **UI重构**: Element Plus → shadcn/ui + Tailwind CSS
3. **状态管理**: Pinia → Zustand + TanStack Query
4. **API对接**: 对接Spring Boot REST API和WebSocket
5. **阅读器优化**: 使用虚拟滚动优化大章节渲染
6. **PWA支持**: 添加Service Worker支持离线阅读

### 8.4 前端目录结构

```
legado-web/
├── app/                           # Next.js App Router
│   ├── layout.tsx                # 根布局
│   ├── page.tsx                  # 首页（书架）
│   ├── globals.css               # 全局样式
│   │
│   ├── (auth)/                   # 认证路由组
│   │   ├── login/page.tsx        # 登录页
│   │   └── register/page.tsx     # 注册页
│   │
│   ├── bookshelf/                # 书架模块
│   │   ├── page.tsx              # 书架主页
│   │   └── layout.tsx            # 书架布局
│   │
│   ├── reader/                   # 阅读器模块
│   │   ├── [bookId]/             # 动态路由
│   │   │   └── page.tsx          # 阅读页
│   │   └── layout.tsx            # 阅读器布局（全屏）
│   │
│   ├── sources/                  # 书源管理
│   │   ├── page.tsx              # 书源列表
│   │   ├── [id]/edit/page.tsx    # 编辑书源
│   │   └── test/page.tsx         # 书源测试
│   │
│   ├── search/                   # 搜索模块
│   │   └── page.tsx              # 搜索结果页
│   │
│   └── settings/                 # 设置页面
│       └── page.tsx              # 用户设置
│
├── components/                    # React组件
│   ├── ui/                       # shadcn/ui 组件
│   │   ├── button.tsx
│   │   ├── dialog.tsx
│   │   ├── dropdown-menu.tsx
│   │   └── ...
│   │
│   ├── bookshelf/                # 书架相关组件
│   │   ├── BookCard.tsx          # 书籍卡片
│   │   ├── BookGrid.tsx          # 书籍网格
│   │   ├── BookList.tsx          # 书籍列表
│   │   └── CategoryFilter.tsx    # 分类筛选
│   │
│   ├── reader/                   # 阅读器组件
│   │   ├── Reader.tsx            # 阅读器主组件
│   │   ├── ChapterContent.tsx    # 章节内容
│   │   ├── ReaderToolbar.tsx     # 阅读工具栏
│   │   ├── ReaderSettings.tsx    # 阅读设置面板
│   │   ├── ChapterList.tsx       # 章节目录
│   │   └── VirtualScroll.tsx     # 虚拟滚动容器
│   │
│   ├── search/                   # 搜索组件
│   │   ├── SearchBar.tsx         # 搜索框
│   │   ├── SearchResult.tsx      # 搜索结果
│   │   └── SourceFilter.tsx      # 书源筛选
│   │
│   └── common/                   # 通用组件
│       ├── Header.tsx            # 顶部导航
│       ├── Sidebar.tsx           # 侧边栏
│       ├── ThemeProvider.tsx     # 主题提供者
│       └── Loading.tsx           # 加载状态
│
├── hooks/                         # 自定义Hooks
│   ├── useAuth.ts                # 认证相关
│   ├── useBooks.ts               # 书籍数据
│   ├── useChapters.ts            # 章节数据
│   ├── useReaderSettings.ts      # 阅读设置
│   └── useWebSocket.ts           # WebSocket连接
│
├── lib/                          # 工具库
│   ├── utils.ts                  # 通用工具
│   ├── api.ts                    # API封装
│   ├── websocket.ts              # WebSocket封装
│   └── storage.ts                # 本地存储
│
├── stores/                        # Zustand状态管理
│   ├── authStore.ts              # 认证状态
│   ├── readerStore.ts            # 阅读器状态
│   └── settingsStore.ts          # 全局设置
│
├── types/                         # TypeScript类型
│   ├── book.ts                   # 书籍类型
│   ├── chapter.ts                # 章节类型
│   ├── source.ts                 # 书源类型
│   └── api.ts                    # API响应类型
│
├── public/                        # 静态资源
│   └── icons/                    # 图标
│
├── next.config.js                # Next.js配置
├── tailwind.config.ts            # Tailwind配置
├── tsconfig.json                 # TypeScript配置
└── package.json                  # 依赖管理
```

### 8.5 核心功能组件设计

#### 8.5.1 阅读器组件架构

```tsx
// app/reader/[bookId]/page.tsx
'use client';

import { useParams } from 'next/navigation';
import { useBook, useChapters } from '@/hooks/useBooks';
import { Reader } from '@/components/reader/Reader';

export default function ReaderPage() {
  const { bookId } = useParams();
  const { data: book } = useBook(bookId as string);
  const { data: chapters } = useChapters(bookId as string);

  return (
    <div className="h-screen w-full bg-background">
      <Reader book={book} chapters={chapters} />
    </div>
  );
}
```

#### 8.5.2 书架页面

```tsx
// app/bookshelf/page.tsx
import { BookGrid } from '@/components/bookshelf/BookGrid';
import { CategoryFilter } from '@/components/bookshelf/CategoryFilter';

export default function BookshelfPage() {
  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">我的书架</h1>
        <CategoryFilter />
      </div>
      <BookGrid />
    </div>
  );
}
```

### 8.6 状态管理设计

```typescript
// stores/readerStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface ReaderState {
  // 阅读设置
  fontSize: number;
  lineHeight: number;
  theme: 'light' | 'dark' | 'sepia';
  pageMode: 'scroll' | 'pagination';
  
  // 当前阅读状态
  currentChapter: number;
  scrollPosition: number;
  
  // Actions
  setFontSize: (size: number) => void;
  setTheme: (theme: 'light' | 'dark' | 'sepia') => void;
  setCurrentChapter: (index: number) => void;
  updateProgress: (chapter: number, position: number) => void;
}

export const useReaderStore = create<ReaderState>()(
  persist(
    (set) => ({
      fontSize: 18,
      lineHeight: 1.8,
      theme: 'light',
      pageMode: 'scroll',
      currentChapter: 0,
      scrollPosition: 0,
      
      setFontSize: (size) => set({ fontSize: size }),
      setTheme: (theme) => set({ theme }),
      setCurrentChapter: (index) => set({ currentChapter: index }),
      updateProgress: (chapter, position) => 
        set({ currentChapter: chapter, scrollPosition: position }),
    }),
    {
      name: 'reader-settings',
    }
  )
);
```

### 8.7 数据获取方案

```typescript
// hooks/useBooks.ts
import { useQuery, useMutation } from '@tanstack/react-query';
import api from '@/lib/api';

export function useBooks() {
  return useQuery({
    queryKey: ['books'],
    queryFn: async () => {
      const { data } = await api.get('/api/v1/books');
      return data;
    },
  });
}

export function useBook(bookId: string) {
  return useQuery({
    queryKey: ['book', bookId],
    queryFn: async () => {
      const { data } = await api.get(`/api/v1/books/${bookId}`);
      return data;
    },
  });
}

export function useChapterContent(bookId: string, chapterIndex: number) {
  return useQuery({
    queryKey: ['chapter', bookId, chapterIndex],
    queryFn: async () => {
      const { data } = await api.get(
        `/api/v1/books/${bookId}/chapters/${chapterIndex}/content`
      );
      return data;
    },
    staleTime: 1000 * 60 * 5, // 5分钟缓存
  });
}
```

---

## 9. 安全设计

### 9.1 认证与授权

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/v1/auth/**").permitAll()
            .antMatchers("/api/v1/public/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

@Component
public class JwtTokenProvider {
    
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("username", user.getUsername())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
}
```

### 9.2 接口限流

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final LoadingCache<String, Integer> requestCounts;
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        String clientId = getClientId(request);
        
        int requests = requestCounts.get(clientId);
        if (requests >= MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            return false;
        }
        
        requestCounts.put(clientId, requests + 1);
        return true;
    }
}
```

---

## 10. 部署方案

### 10.1 开发环境

```yaml
# docker-compose.dev.yml
version: '3.8'
services:
  backend:
    build: ./legado-server
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data  # SQLite数据目录挂载
  
  frontend:
    build: ./legado-web
    ports:
      - "3000:80"
    depends_on:
      - backend
```

### 10.2 生产环境

```yaml
# docker-compose.prod.yml
version: '3.8'
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
  
  backend:
    image: legado-server:latest
    volumes:
      - ./data:/app/data  # 持久化SQLite数据库
    environment:
      SPRING_PROFILES_ACTIVE: prod
  
  # 无需MySQL/Redis，使用SQLite内置存储

volumes:
  legado_data:  # SQLite数据卷
```

### 10.3 数据备份

由于使用SQLite单文件存储，备份非常简单：

```bash
#!/bin/bash
# backup.sh - 数据库备份脚本

BACKUP_DIR="/backups"
DB_FILE="/app/data/legado.db"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份
cp "$DB_FILE" "$BACKUP_DIR/legado_$DATE.db"

# 保留最近30天的备份
find "$BACKUP_DIR" -name "legado_*.db" -mtime +30 -delete

echo "Backup completed: legado_$DATE.db"
```

---

## 11. 迁移计划

### 11.1 阶段划分

| 阶段 | 时间 | 任务 | 产出 |
|------|------|------|------|
| 第1阶段 | 2周 | 基础架构搭建 | 项目框架、SQLite配置 |
| 第2阶段 | 3周 | 核心功能实现 | 书源引擎、书籍管理 |
| 第3阶段 | 2周 | 前端对接改造 | API对接、界面优化 |
| 第4阶段 | 2周 | 高级功能 | 缓存、搜索优化 |
| 第5阶段 | 1周 | 测试与部署 | 测试用例、部署文档 |

### 11.2 数据迁移

**从原Android SQLite迁移到新Spring Boot SQLite：**

```java
@Component
public class DataMigrationService {
    
    /**
     * 从原Android数据库文件迁移数据
     * 直接复制数据库文件即可，因为都是SQLite格式
     */
    public void migrateFromAndroid(String sourceDbPath, String targetDbPath) {
        // 直接复制SQLite数据库文件
        // 两个数据库结构相同，无需转换
        Files.copy(
            Paths.get(sourceDbPath),
            Paths.get(targetDbPath),
            StandardCopyOption.REPLACE_EXISTING
        );
        
        log.info("数据库迁移完成: {} -> {}", sourceDbPath, targetDbPath);
    }
    
    /**
     * 如果需要迁移特定表数据
     */
    public void migrateSpecificTables(String androidDbPath) {
        // 使用SQLite JDBC连接到原数据库
        String jdbcUrl = "jdbc:sqlite:" + androidDbPath;
        
        try (Connection sourceConn = DriverManager.getConnection(jdbcUrl);
             Connection targetConn = dataSource.getConnection()) {
            
            // 迁移书籍数据
            migrateBooks(sourceConn, targetConn);
            
            // 迁移书源数据
            migrateBookSources(sourceConn, targetConn);
            
            // 迁移阅读记录
            migrateReadRecords(sourceConn, targetConn);
            
        } catch (SQLException e) {
            log.error("数据迁移失败", e);
            throw new MigrationException("数据迁移失败", e);
        }
    }
}
```

---

## 12. 技术风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| JavaScript引擎性能 | 高 | 使用GraalVM替代Rhino，必要时实现自定义DSL |
| 并发抓取被封 | 中 | 实现请求限流、IP代理池、User-Agent轮换 |
| 书源兼容性问题 | 高 | 建立书源测试框架，维护书源白名单 |
| SQLite并发性能 | 中 | 使用连接池(HikariCP)，写操作串行化，读操作并发 |
| SQLite文件损坏 | 中 | 定期备份，启用WAL模式提高可靠性 |
| 数据迁移失败 | 高 | 保留原数据库备份，支持回滚 |

### 12.1 SQLite性能优化

```yaml
# SQLite性能优化配置
spring:
  datasource:
    url: jdbc:sqlite:./data/legado.db?journal_mode=WAL&synchronous=NORMAL&cache_size=10000
    driver-class-name: org.sqlite.JDBC
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

**优化策略：**
1. **WAL模式**: 启用Write-Ahead Logging，提高并发性能
2. **连接池**: 使用HikariCP管理连接
3. **缓存**: 增大SQLite缓存大小
4. **索引**: 为常用查询字段添加索引
5. **异步写**: 将写操作放入队列异步执行

---

## 13. 总结

本方案将Legado Android应用改造为基于JDK8 + Spring Boot的前后端分离架构：

**核心改进：**
1. 后端使用Spring Boot提供RESTful API和WebSocket服务
2. **数据库保持SQLite**，与原项目兼容，无需额外数据库服务
3. 书源引擎从Rhino迁移到GraalVM JS
4. 前端保持Vue 3技术栈，对接新API
5. 使用Caffeine本地缓存提升性能

**SQLite方案优势：**
- 零配置，开箱即用
- 单文件存储，便于备份和迁移
- 与原Android项目数据库格式兼容
- 适合个人/单机部署场景
- 无需维护额外的数据库服务

**预期收益：**
- 支持Web、移动端等多平台访问
- 更好的性能和可扩展性
- 便于维护和升级
- 支持多用户和云端同步
- 数据库轻量便携

---

*文档版本: 1.1*  
*创建日期: 2026-02-15*  
*最后更新: 2026-02-15*  
*更新说明: 将数据库从MySQL改为SQLite，简化架构设计*
