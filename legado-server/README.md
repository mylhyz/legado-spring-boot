# Legado Server - 开源阅读后端服务

基于 Spring Boot + SQLite 的现代化小说阅读后端服务，支持书源规则解析、全网搜索、在线阅读等功能。

## 功能特性

### 核心功能
- 📚 **书架管理** - 添加、删除、分类管理书籍
- 🔍 **全网搜索** - 基于书源的多站点并行搜索
- 📖 **在线阅读** - 章节列表、内容获取、阅读进度同步
- ⚙️ **书源管理** - 书源导入、编辑、测试
- 🔐 **用户认证** - JWT Token 认证，支持用户注册登录
- ☁️ **实时同步** - WebSocket 支持阅读进度多端同步

### 技术亮点
- **书源引擎** - 基于 GraalVM 的 JavaScript 引擎解析书源规则
- **并行搜索** - 多书源并发搜索，提升搜索效率
- **本地缓存** - Caffeine 缓存优化章节内容读取性能
- **轻量部署** - SQLite 单文件数据库，无需额外数据库服务

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **JDK** | Java | 1.8 |
| **框架** | Spring Boot | 2.7.18 |
| **ORM** | Spring Data JPA | 2.7.x |
| **数据库** | SQLite | 3.x |
| **缓存** | Caffeine | 3.x |
| **JS引擎** | GraalVM JS | 22.x |
| **HTML解析** | Jsoup | 1.17.x |
| **HTTP客户端** | OkHttp | 4.12.x |
| **安全** | Spring Security + JWT | 5.7.x / 0.11.x |
| **文档** | SpringDoc OpenAPI | 1.7.x |

## 项目结构

```
legado-server/
├── pom.xml                                    # 父POM
├── legado-api/                                # API接口模块
│   ├── src/main/java/io/legado/api/
│   │   ├── LegadoApplication.java            # 启动类
│   │   ├── config/                           # 配置类
│   │   │   ├── SecurityConfig.java          # 安全配置
│   │   │   ├── JwtTokenProvider.java        # JWT工具
│   │   │   ├── JwtAuthenticationFilter.java # JWT过滤器
│   │   │   ├── WebSocketConfig.java         # WebSocket配置
│   │   │   ├── CorsConfig.java              # 跨域配置
│   │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   │   ├── controller/                       # 控制器
│   │   │   ├── AuthController.java          # 认证接口
│   │   │   ├── BookController.java          # 书籍接口
│   │   │   └── BookSourceController.java    # 书源接口
│   │   ├── dto/                              # 数据传输对象
│   │   │   ├── ApiResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── LoginResponse.java
│   │   └── websocket/                        # WebSocket
│   │       └── ReaderWebSocketController.java
│   └── pom.xml
│
├── legado-core/                               # 核心业务模块
│   ├── src/main/java/io/legado/core/
│   │   ├── service/                          # 业务服务
│   │   │   ├── BookService.java             # 书籍服务
│   │   │   ├── BookSearchService.java       # 搜索服务
│   │   │   ├── BookSourceService.java       # 书源服务
│   │   │   └── UserService.java             # 用户服务
│   │   ├── booksource/                       # 书源引擎
│   │   │   ├── BookSourceEngine.java        # 规则解析引擎
│   │   │   ├── SearchRule.java              # 搜索规则
│   │   │   ├── BookInfoRule.java            # 书籍信息规则
│   │   │   ├── TocRule.java                 # 目录规则
│   │   │   └── ContentRule.java             # 正文规则
│   │   ├── utils/                            # 工具类
│   │   │   └── HttpClient.java              # HTTP客户端
│   │   ├── dto/                              # DTO
│   │   │   └── SearchResultDto.java
│   │   └── config/                           # 配置
│   │       ├── AsyncConfig.java             # 异步配置
│   │       └── CacheConfig.java             # 缓存配置
│   └── pom.xml
│
├── legado-model/                              # 数据模型模块
│   ├── src/main/java/io/legado/model/
│   │   ├── entity/                           # JPA实体
│   │   │   ├── Book.java                    # 书籍
│   │   │   ├── BookChapter.java             # 章节
│   │   │   ├── BookSource.java              # 书源
│   │   │   ├── BookGroup.java               # 分组
│   │   │   ├── ReplaceRule.java             # 替换规则
│   │   │   ├── Bookmark.java                # 书签
│   │   │   ├── Cookie.java                  # Cookie
│   │   │   ├── RssSource.java               # RSS源
│   │   │   ├── RssArticle.java              # RSS文章
│   │   │   ├── User.java                    # 用户
│   │   │   └── ReadConfig.java              # 阅读配置
│   │   ├── repository/                       # 数据访问层
│   │   │   ├── BookRepository.java
│   │   │   ├── BookChapterRepository.java
│   │   │   ├── BookSourceRepository.java
│   │   │   ├── BookGroupRepository.java
│   │   │   ├── ReplaceRuleRepository.java
│   │   │   ├── BookmarkRepository.java
│   │   │   ├── CookieRepository.java
│   │   │   └── UserRepository.java
│   │   └── converter/                        # 类型转换器
│   │       └── ReadConfigConverter.java
│   └── pom.xml
│
├── legado-common/                             # 公共模块
│   └── src/main/java/io/legado/common/
│
└── data/                                      # SQLite数据目录
    └── legado.db                             # 数据库文件
```

## 快速开始

### 环境要求
- JDK 1.8 或更高版本
- Maven 3.6+ 
- 内存: 最低 512MB，推荐 1GB+

### 1. 克隆项目

```bash
cd /Users/mylhyz/Downloads/legado-3.25/legado-server
```

### 2. 编译项目

```bash
# 清理并编译所有模块
mvn clean compile -DskipTests

# 或者打包（包含依赖）
mvn clean package -DskipTests
```

### 3. 运行项目

#### 方式一：使用 Maven 插件（推荐开发时使用）

```bash
cd legado-api
mvn spring-boot:run
```

#### 方式二：运行 JAR 包（推荐生产环境）

```bash
# 打包后执行
cd legado-api/target
java -jar legado-api-1.0.0-SNAPSHOT.jar

# 或者指定配置文件
java -jar legado-api-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4. 验证运行

服务启动后，访问以下地址：

- **API 文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health
- **书架接口**: http://localhost:8080/api/v1/books

### 5. 首次使用

#### 注册用户
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456",
    "email": "admin@example.com"
  }'
```

#### 登录获取 Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

返回示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "admin",
      "nickname": "admin"
    }
  },
  "timestamp": 1708012800000
}
```

#### 使用 Token 访问 API
```bash
# 获取书架列表
curl http://localhost:8080/api/v1/books \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

## API 接口文档

### 认证接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 注册 | POST | /api/v1/auth/register | 用户注册 |
| 登录 | POST | /api/v1/auth/login | 用户登录 |
| 当前用户 | GET | /api/v1/auth/me | 获取当前用户信息 |

### 书籍接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 书架列表 | GET | /api/v1/books | 获取所有书籍 |
| 书籍详情 | GET | /api/v1/books/{id} | 获取书籍详情 |
| 添加书籍 | POST | /api/v1/books | 添加书籍到书架 |
| 从书源添加 | POST | /api/v1/books/from-source | 从书源添加书籍 |
| 删除书籍 | DELETE | /api/v1/books/{id} | 从书架删除 |
| 章节列表 | GET | /api/v1/books/{id}/chapters | 获取章节列表 |
| 章节内容 | GET | /api/v1/books/{id}/chapters/{index}/content | 获取章节内容 |
| 更新进度 | PUT | /api/v1/books/{id}/progress | 更新阅读进度 |
| 搜索书籍 | GET | /api/v1/books/search?keyword=xxx | 全网搜索 |

### 书源接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 书源列表 | GET | /api/v1/sources | 获取所有书源 |
| 书源详情 | GET | /api/v1/sources/{id} | 获取书源详情 |
| 添加书源 | POST | /api/v1/sources | 添加新书源 |
| 批量添加 | POST | /api/v1/sources/batch | 批量导入书源 |
| 更新书源 | PUT | /api/v1/sources/{id} | 更新书源 |
| 删除书源 | DELETE | /api/v1/sources/{id} | 删除书源 |
| 启用/禁用 | PUT | /api/v1/sources/{id}/toggle | 切换书源状态 |

## 数据库设计

### 核心表结构

**books（书籍表）**
```sql
- id: 主键
- book_url: 书籍URL（唯一）
- name: 书名
- author: 作者
- cover_url: 封面URL
- intro: 简介
- origin: 书源URL
- dur_chapter_index: 当前章节索引
- dur_chapter_pos: 当前阅读位置
- total_chapter_num: 总章节数
```

**book_chapters（章节表）**
```sql
- id: 主键
- book_id: 书籍ID（外键）
- chapter_index: 章节索引
- title: 章节标题
- url: 章节链接
- content: 章节内容
```

**book_sources（书源表）**
```sql
- id: 主键
- source_name: 书源名称
- source_url: 书源URL
- search_url: 搜索地址
- rule_search: 搜索规则（JSON）
- rule_book_info: 书籍信息规则（JSON）
- rule_toc: 目录规则（JSON）
- rule_content: 正文规则（JSON）
- enabled: 是否启用
```

**users（用户表）**
```sql
- id: 主键
- username: 用户名（唯一）
- password: 密码（加密）
- email: 邮箱
- nickname: 昵称
- roles: 角色
- enabled: 是否启用
```

## 配置文件

### application.yml

```yaml
# 服务端口
server:
  port: 8080

# 数据库配置（SQLite）
spring:
  datasource:
    url: jdbc:sqlite:./data/legado.db?journal_mode=WAL&synchronous=NORMAL
    driver-class-name: org.sqlite.JDBC
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5

  # JPA配置
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: update
    show-sql: false

  # 缓存配置
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=1h

# JWT配置
jwt:
  secret: legado-secret-key-change-in-production
  expiration: 86400000  # 24小时

# CORS配置
legado:
  cors:
    allowed-origins: http://localhost:3000,http://localhost:3001
```

## 书源规则说明

书源规则使用 JSON 格式存储，支持以下字段：

### 搜索规则
```json
{
  "bookList": "selector",        // 书籍列表选择器
  "name": "selector",            // 书名选择器
  "author": "selector",          // 作者选择器
  "intro": "selector",           // 简介选择器
  "coverUrl": "selector@attr",   // 封面选择器（@src获取属性）
  "bookUrl": "selector@href",    // 详情链接选择器
  "latestChapter": "selector",   // 最新章节选择器
  "wordCount": "selector",       // 字数选择器
  "kind": "selector"             // 分类选择器
}
```

### 正文规则
```json
{
  "content": "selector",         // 正文内容选择器
  "nextContentUrl": "selector@href"  // 下一页链接
}
```

## 开发指南

### 添加新的 API 接口

1. 在 `legado-api` 模块的 `controller` 包创建 Controller
2. 在 `legado-core` 模块的 `service` 包实现业务逻辑
3. 在 `legado-model` 模块添加实体和 Repository（如需要）

### 书源引擎扩展

修改 `BookSourceEngine.java` 可支持更多解析规则：
- XPath 解析
- JSONPath 解析
- 正则表达式替换
- JavaScript 处理

### 缓存配置

默认使用 Caffeine 本地缓存，可在 `CacheConfig.java` 中调整：
```java
Caffeine.newBuilder()
    .maximumSize(10000)           // 最大缓存条目
    .expireAfterWrite(1, TimeUnit.HOURS)  // 过期时间
```

## 生产部署

### 使用 Docker

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY legado-api/target/legado-api-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

构建并运行：
```bash
docker build -t legado-server .
docker run -p 8080:8080 -v $(pwd)/data:/app/data legado-server
```

### 使用 docker-compose

```yaml
version: '3.8'
services:
  legado:
    build: ./legado-server
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

## 常见问题

### 1. 编译失败，提示找不到依赖
```bash
# 清理Maven缓存并重新编译
mvn clean install -U
```

### 2. SQLite 数据库文件位置
默认在项目根目录的 `data/legado.db`，可通过修改 `application.yml` 中的 `spring.datasource.url` 更改。

### 3. JWT Token 过期
默认24小时，修改 `jwt.expiration` 配置（单位：毫秒）。

### 4. 跨域问题
修改 `legado.cors.allowed-origins` 配置，添加前端域名。

### 5. 内存不足
```bash
# 启动时指定JVM参数
java -Xms512m -Xmx1g -jar legado-api-1.0.0-SNAPSHOT.jar
```

## 许可证

本项目基于 GPL-3.0 许可证开源。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 相关项目

- [Legado](https://github.com/gedoor/legado) - 开源阅读 Android 客户端
- [Legado Web](https://github.com/gedoor/legado-web) - 官方 Web 前端

---

**注意**: 本项目仅供学习研究使用，请勿用于商业用途。
