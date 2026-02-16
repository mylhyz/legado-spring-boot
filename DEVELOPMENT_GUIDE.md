# Legado 前后端分离项目 - 开发调试指南

本指南将指导您一步步启动 Legado 前后端分离项目，在开发环境中进行测试。

---

## 📋 目录

1. [环境准备](#1-环境准备)
2. [项目结构](#2-项目结构)
3. [后端服务启动](#3-后端服务启动)
4. [前端服务启动](#4-前端服务启动)
5. [功能验证](#5-功能验证)
6. [常见问题](#6-常见问题)

---

## 1. 环境准备

### 1.1 必需环境

| 工具 | 版本要求 | 用途 |
|------|----------|------|
| **JDK** | 1.8+ | Java 开发环境 |
| **Maven** | 3.6+ | 项目构建工具 |
| **Node.js** | 18.x+ | 前端运行环境 |
| **npm** | 9.x+ | 包管理工具 |
| **Git** | - | 版本控制 |

### 1.2 环境检查

打开终端，运行以下命令检查环境：

```bash
# 检查 JDK
java -version
# 应显示：java version "1.8.0_xxx" 或更高版本

# 检查 Maven
mvn -version
# 应显示：Apache Maven 3.6.x 或更高版本

# 检查 Node.js
node -v
# 应显示：v18.x.x 或更高版本

# 检查 npm
npm -v
# 应显示：9.x.x 或更高版本
```

### 1.3 环境安装（如未安装）

#### macOS

```bash
# 使用 Homebrew 安装
brew install openjdk@8
brew install maven
brew install node

# 设置 JDK 环境变量
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)' >> ~/.zshrc
source ~/.zshrc
```

#### Windows

1. **JDK**: 下载安装 [JDK 8](https://www.oracle.com/java/technologies/downloads/)
2. **Maven**: 下载解压 [Maven](https://maven.apache.org/download.cgi)，添加到系统 PATH
3. **Node.js**: 下载安装 [Node.js](https://nodejs.org/)

#### Linux (Ubuntu)

```bash
# 安装 JDK 8
sudo apt update
sudo apt install openjdk-8-jdk

# 安装 Maven
sudo apt install maven

# 安装 Node.js
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs
```

---

## 2. 项目结构

```
legado-3.25/
├── legado-server/              # 后端项目（Spring Boot）
│   ├── legado-api/             # API 接口模块
│   ├── legado-core/            # 核心业务模块
│   ├── legado-model/           # 数据模型模块
│   ├── legado-common/          # 公共模块
│   ├── data/                   # SQLite 数据目录
│   │   └── legado.db           # 数据库文件（运行时创建）
│   └── pom.xml                 # Maven 配置文件
│
├── legado-web/                 # 前端项目（Next.js + React）
│   ├── app/                    # 页面组件
│   │   ├── bookshelf/          # 书架页面
│   │   ├── search/             # 搜索页面
│   │   ├── reader/[bookId]/    # 阅读器页面
│   │   ├── sources/            # 书源管理页面
│   │   ├── settings/           # 设置页面
│   │   ├── login/              # 登录页面
│   │   └── layout.tsx          # 根布局
│   ├── components/             # UI 组件
│   ├── stores/                 # 状态管理
│   ├── lib/                    # 工具库
│   └── package.json            # npm 配置
│
└── README.md                   # 项目说明
```

---

## 3. 后端服务启动

### 3.1 进入后端目录

```bash
cd legado-3.25/legado-server
```

### 3.2 编译项目

```bash
# 清理并编译所有模块
mvn clean install -DskipTests
```

**预期输出**：
```
[INFO] Building legado-server 1.0.0-SNAPSHOT
[INFO] --------------------------------[ pom ]---------------------------------
[INFO] 
[INFO] legado-api ........................................ SUCCESS [ 12.345 s]
[INFO] legado-server ...................................... SUCCESS [  0.123 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 3.3 启动后端服务

#### 方式一：使用 Maven 插件（推荐开发模式）

```bash
cd legado-api
mvn spring-boot:run
```

#### 方式二：使用已打包的 JAR

```bash
cd legado-api/target
java -jar legado-api-1.0.0-SNAPSHOT.jar
```

### 3.4 验证后端启动

**成功标志**：
- 控制台显示：`Started LegadoApplication in X.XXX seconds`
- 访问：`http://localhost:8080/api/v1/books`
- 预期返回：`{ "code": 401, "message": "未登录或Token已过期" }`（需要认证）

### 3.5 后端配置（可选）

编辑 `legado-api/src/main/resources/application.yml`：

```yaml
server:
  port: 8080  # 修改后端端口

legado:
  data:
    path: ./data  # 数据库文件目录
  cors:
    allowed-origins: http://localhost:3000  # 前端地址
```

---

## 4. 前端服务启动

### 4.1 进入前端目录

打开新终端窗口：

```bash
cd legado-3.25/legado-web
```

### 4.2 安装依赖

```bash
# 如果首次运行或 package.json 有更新
npm install
```

**预期输出**：
```
added 272 packages in 45s
```

### 4.3 配置 API 地址

编辑 `legado-web/.env.local`（创建新文件）：

```bash
# 创建环境变量文件
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > .env.local
```

### 4.4 启动前端开发服务器

```bash
npm run dev
```

**预期输出**：
```
> legado-web@1.0.0 dev
> next dev

   ▲ Next.js 14.1.0
   - Local:        http://localhost:3000
   - Environments: .env.local

 ✓ Ready in 1254ms
```

### 4.5 验证前端启动

- 浏览器访问：`http://localhost:3000`
- 应自动跳转到登录页面：`http://localhost:3000/login`

---

## 5. 功能验证

### 5.1 用户认证测试

1. **打开浏览器**：访问 http://localhost:3000
2. **注册新用户**：
   - 点击"注册"
   - 输入用户名（如：`testuser`）
   - 输入密码（至少6位，如：`123456`）
   - 可选：填写邮箱
   - 点击"注册"

3. **登录验证**：
   - 输入用户名和密码
   - 点击"登录"
   - 应跳转到书架页面

### 5.2 书架功能测试

1. **添加书籍**：
   - 点击右上角"添加书籍"
   - 跳转到搜索页面

2. **搜索书籍**：
   - 输入书名（如：`斗破苍穹`）
   - 点击"搜索"
   - 等待搜索结果显示

3. **添加书籍到书架**：
   - 点击搜索结果中的"添加"按钮
   - 成功提示后自动返回书架

4. **开始阅读**：
   - 点击书籍封面
   - 进入阅读器页面

### 5.3 阅读器测试

1. **阅读功能**：
   - 查看章节内容
   - 点击"上一章"/"下一章"切换
   - 点击目录按钮查看章节列表

2. **主题切换**：
   - 点击主题按钮切换白天/夜间/护眼模式

3. **字体调节**：
   - 点击设置按钮
   - 调节字体大小和行间距

4. **进度保存**：
   - 阅读若干章节后，关闭页面
   - 重新打开书籍，应回到上次阅读位置

### 5.4 书源管理测试

1. **进入书源管理**：
   - 点击顶部导航"书源"
   - 查看书源列表

2. **添加书源**：
   - 点击"添加书源"
   - 填写书源信息：
     - 书源名称：测试书源
     - 书源URL：https://example.com
     - 搜索URL：https://example.com/search?wd={{key}}
   - 点击"保存"

3. **测试书源**：
   - 点击书源卡片上的"测试"按钮
   - 查看测试结果

4. **导入/导出书源**：
   - 点击"导入"按钮，选择 JSON 文件
   - 点击书源上的"导出"按钮

### 5.5 系统设置测试

1. **进入设置**：
   - 点击顶部导航"设置"

2. **修改阅读设置**：
   - 切换到"阅读设置"标签
   - 调节字体大小和行间距

3. **修改外观**：
   - 切换到"外观"标签
   - 选择默认主题和字体

4. **退出登录**：
   - 点击左侧"退出登录"
   - 应返回登录页面

---

## 6. 常见问题

### 6.1 后端启动问题

#### Q: 端口 8080 被占用

**错误信息**：
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案**：
```bash
# 查找占用端口的进程
lsof -i :8080

# 结束进程
kill -9 <PID>

# 或修改后端端口
# 编辑 application.yml，修改 server.port
```

#### Q: Maven 编译失败

**错误信息**：
```
Could not find artifact xxx
```

**解决方案**：
```bash
# 清理本地仓库缓存
rm -rf ~/.m2/repository

# 重新编译
mvn clean install -U
```

### 6.2 前端启动问题

#### Q: npm install 失败

**错误信息**：
```
npm ERR! code ECONNREFUSED
```

**解决方案**：
```bash
# 使用淘宝镜像
npm config set registry https://registry.npmmirror.com

# 重新安装
npm install
```

#### Q: 前端无法访问后端

**现象**：登录/注册提示"请求失败"

**解决方案**：
1. 检查后端是否启动：`curl http://localhost:8080/api/v1/books`
2. 检查前端配置：确认 `.env.local` 中 API URL 正确
3. 检查跨域配置：后端 `application.yml` 中 `cors.allowed-origins` 包含 `http://localhost:3000`

### 6.3 数据库问题

#### Q: 数据库文件在哪

**位置**：`legado-server/data/legado.db`

**查看方式**：
```bash
# 使用 SQLite 命令行
sqlite3 legado-server/data/legado.db

# 查看表结构
.tables
.schema book

# 查看数据
SELECT * FROM book;
```

#### Q: 重置数据库

**解决方案**：
```bash
# 停止后端服务
# 删除数据库文件
rm legado-server/data/legado.db

# 重新启动后端，会自动创建新数据库
```

### 6.4 热重载问题

#### 后端热重载

修改 Java 代码后，需要重新编译：
```bash
# 在 legado-api 目录下
mvn spring-boot:run
```

#### 前端热重载

Next.js 开发服务器支持自动热重载，修改代码后会自动刷新浏览器。

---

## 7. 调试技巧

### 7.1 查看后端日志

```bash
# 实时查看日志
tail -f legado-api/target/spring.log

# 或使用 IDE 的 Console 查看
```

### 7.2 查看前端控制台

- 浏览器按 `F12` 打开开发者工具
- 切换到 "Console" 标签查看错误信息
- 切换到 "Network" 标签查看 API 请求

### 7.3 API 测试

使用 curl 或 Postman 测试后端接口：

```bash
# 登录
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 获取书籍列表（需替换 token）
curl http://localhost:8080/api/v1/books \
  -H "Authorization: Bearer <token>"
```

### 7.4 数据监控

```bash
# 查看 SQLite 数据库
sqlite3 legado-server/data/legado.db "SELECT * FROM book_source;"
```

---

## 8. 生产部署预览

### 8.1 后端打包

```bash
cd legado-server
mvn clean package -DskipTests

# 生成的 JAR 文件
legado-api/target/legado-api-1.0.0-SNAPSHOT.jar
```

### 8.2 前端打包

```bash
cd legado-web
npm run build

# 生成的静态文件
legado-web/out/
```

---

## 9. 参考文档

- [后端功能说明](legado-server/README.md)
- [前端功能说明](legado-web/FEATURES.md)
- [架构设计文档](ARCH.md)
- [Next.js 文档](https://nextjs.org/docs)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)

---

## 10. 快速启动命令速查

```bash
# 终端 1 - 启动后端
cd legado-server/legado-api
mvn spring-boot:run

# 终端 2 - 启动前端
cd legado-web
npm run dev

# 访问 http://localhost:3000
```

**默认账号**：
- 后端：`http://localhost:8080`
- 前端：`http://localhost:3000`

祝您调试顺利！如有问题请查看控制台日志或参考常见问题章节。


## 调试进度

1，书源管理页面 yuedu://rsssource/importonline?src=http://yuedu.miaogongzi.net/shuyuan/miaogongziDY.json
2，书源管理首页 http://yuedu.miaogongzi.net/gx.html
3，管理页面内导入书源 yuedu://booksource/importonline?src=https://shuyuan.nyasama.cc/cdn/5f626361539d546e6fa3a02b24598284.json
