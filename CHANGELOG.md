**2022/10/02**

* 更新cronet: 106.0.5249.79
* 正文选择菜单朗读按钮长按可切换朗读选择内容和从选择开始处一直朗读
* 源编辑输入框设置最大行数12,在行数特别多的时候更容易滚动到其它输入
* 修复某些情况下无法搜索到标题的bug，净化规则较多的可能会降低搜索速度 by Xwite
* 修复文件类书源换源后阅读bug by Xwite
* Cronet 支持DnsHttpsSvcb by g2s20150909
* 修复web进度同步问题 by 821938089
* 启用混淆以减小app大小 有bug请带日志反馈
* 其它一些优化

---

**2026/02/16 - 前后端分离项目启动问题修复**

### 后端修复 (legado-server)

* **修复数据库路径错误**
  * 问题：SQLite 无法找到相对路径 `./data/legado.db`，导致启动失败
  * 修复：修改 `application.yml`，使用绝对路径 `${user.home}/.legado/legado.db`
  * 文件：`legado-api/src/main/resources/application.yml`

* **修复 Hibernate 版本不兼容**
  * 问题：使用了 Hibernate 6.x 的 `hibernate-community-dialects:6.4.4.Final`，但 Spring Boot 2.7 内置 Hibernate 5.6.x，导致方言类初始化失败
  * 修复：更换为兼容的 SQLite 方言库 `com.github.gwenn:sqlite-dialect:0.1.2`
  * 更新方言类：`org.hibernate.community.dialect.SQLiteDialect` → `org.sqlite.hibernate.dialect.SQLiteDialect`
  * 文件：`pom.xml`, `legado-model/pom.xml`, `application.yml`

* **修复实体类索引配置错误**
  * 问题：`Book.java` 中索引定义使用了 Java 属性名 `groupId`，但数据库列名实际为 `group_id`，导致 Hibernate 启动失败
  * 修复：将 `@Index(columnList = "groupId")` 改为 `@Index(columnList = "group_id")`
  * 文件：`legado-model/src/main/java/io/legado/model/entity/Book.java`

### 前端开发 (legado-web)

* **新增设置页面** (`/settings`)
  * 个人资料管理
  * 阅读设置（字体大小、行间距、阅读模式）
  * 外观设置（主题、字体选择）
  * 通知设置（推送开关）
  * 安全设置（修改密码、退出登录）

* **新增书源管理页面** (`/sources`)
  * 书源列表展示与搜索
  * 书源添加与编辑（支持多标签页配置）
  * 书源测试功能
  * 书源导入/导出（JSON格式）
  * 分组筛选功能

* **新增 UI 组件**
  * Label, Switch, Select, Badge, Textarea, Tabs 组件

* **优化 Header 组件**
  * 登录页面不再显示顶部导航栏
