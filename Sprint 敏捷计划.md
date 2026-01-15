太好了，这几个选型一确定（**Java 21 + Maven + MyBatis/MBG + Flyway + Next App Router + 全容器化 + 同域部署**），你的开发节奏就可以按“每周都能上线一个可演示增量”来跑了。下面是我基于你 MVP 设计文档重新校准后的 **4×1 周 Sprint 敏捷计划**（更贴近你现在的工具链与落地方式）。

---

## Sprint 总览（4 个 Sprint × 1 周）

|Sprint|周期|目标（Outcome）|关键交付物（可 Demo）|
|---|---|---|---|
|Sprint 1|Week 1|**公开阅读闭环**：DB→API→Next 页面跑通|`docker compose up` 一键启动；Flyway V1；MBG 生成 posts/tags；Public 文章列表/详情；Next 首页/详情页（App Router）|
|Sprint 2|Week 2|**管理端闭环**：只有你能发文（JWT）|admin_users + 登录 JWT；Admin 文章 CRUD + publish/unpublish；Swagger 或最小 `/admin` 可操作|
|Sprint 3|Week 3|**匿名评论闭环**：提交待审 + 后台审核 + 前台展示|visitor_id 机制（cookie+header）；评论提交 PENDING；后台审核 approve/reject/delete；前台仅 APPROVED|
|Sprint 4|Week 4|**匿名点赞 + 上线加固**：文章/评论点赞 toggle + 同域部署|likes 表（部分唯一索引）；点赞 toggle 稳定；Nginx/Caddy 同域反代 `/api`；基础安全/XSS 加固；生产 Compose|

---

## 贯穿全程的工程约定（建议你 Sprint 1 就落实）

- **Repo 结构（单仓库）**
    
    - `/server`（Spring Boot）
        
    - `/web`（Next.js App Router）
        
    - `/deploy`（nginx/caddy 配置、compose 生产版）
        
- **MyBatis/MBG 约定**
    
    - MBG **只生成基础 CRUD**（不要手改 generated 目录）
        
    - 复杂 SQL：全部放 `*MapperExt.java + *MapperExt.xml`（自定义目录）
        
- **Flyway 约定**
    
    - 迁移脚本：`V1__init.sql`、`V2__admin.sql`…
        
    - **部分唯一索引**、复杂约束只写在 Flyway SQL（不要指望 MBG/JPA 注解）
        

---

## Sprint 1 Backlog（公开阅读 + 基建跑通）

### 用户故事（按优先级）

- **[H]** 访客可浏览已发布文章列表/详情（仅 PUBLISHED）
    
- **[M]** 访客可看到标签列表，并按标签过滤文章
    

### 技术任务（Bullet 形式，适合直接录入任务工具）

**高优先级 [H]**

- **[H] Compose 本地开发环境**
    
    - `postgres` 服务：固定版本、持久化卷、仅内部网络暴露
        
    - `server` 服务：能连上 DB（环境变量注入）
        
    - `web` 服务：能访问 server（容器网络内通过 service name）
        
- **[H] Flyway V1：基础表**
    
    - `posts / tags / post_tags`（含索引：`posts(status,published_at)`、`tags(slug)`）
        
    - 插入最小 seed（2-3 篇已发布文章，便于联调）
        
- **[H] MBG 首次生成（CRUD）**
    
    - 生成 posts/tags/post_tags 的 Model/Mapper/XML
        
    - 建立 `MapperExt` 扩展目录与命名规范（先建空壳也行）
        
- **[H] Public API（只做读）**
    
    - `GET /api/v1/posts`（分页：limit/offset）
        
    - `GET /api/v1/posts/{id}`
        
    - `GET /api/v1/tags`
        
- **[H] Next.js App Router 页面**
    
    - 首页：文章列表
        
    - 详情页：文章正文渲染（先不追求 Markdown 完整能力）
        
    - **请求策略**：客户端请求用相对路径 `/api/v1/...`（为同域部署铺路）
        

**中优先级 [M]**

- **[M] 标签过滤文章（Ext SQL）**
    
    - `GET /api/v1/posts?tag=java`：join `post_tags/tags` 过滤（写在 `PostMapperExt`）
        

**低优先级 [L]**

- **[L] 最小可观测性**
    
    - 请求日志 + 统一错误响应结构（前端排错省时）
        

**Sprint 1 DoD（Definition of Done）**

- `docker compose up` 后：打开首页能看到文章列表，点击能看详情；API 可用并可分页。
    

---

## Sprint 2 Backlog（Admin 登录 + 发文/发布）

### 用户故事

- **[H]** 站长可登录后台（JWT）
    
- **[H]** 站长可创建/编辑文章、发布/下线（草稿状态）
    
- **[M]** 站长可给文章绑定标签（slug）
    

### 技术任务

**高优先级 [H]**

- **[H] Flyway V2：admin_users**
    
    - 表结构：`username unique`、`password_hash(bcrypt)`、`is_active`、`last_login_at`
        
    - 初始化一个 admin（SQL seed 或启动时检查创建）
        
- **[H] Spring Security + JWT（只保护 /api/v1/admin/**）**
    
    - `POST /api/v1/admin/auth/login`：验证密码→签发 JWT
        
    - JWT Filter：只拦 admin 路径，Public 不受影响
        
- **[H] Admin Post API（写入 + 状态机）**
    
    - `POST /api/v1/admin/posts`（创建草稿）
        
    - `PUT /api/v1/admin/posts/{id}`（更新）
        
    - `POST /api/v1/admin/posts/{id}/publish`（写 `published_at`、置 PUBLISHED）
        
    - `POST /api/v1/admin/posts/{id}/unpublish`（回 DRAFT）
        
    - `GET /api/v1/admin/posts`（后台列表：含草稿/已发布）
        

**中优先级 [M]**

- **[M] 标签写入（推荐 Ext SQL）**
    
    - 输入 tag slugs：不存在则插入 tags，再写 post_tags（幂等）
        
    - 注意并发下的 tag 唯一冲突处理（靠 DB unique + 捕获冲突）
        
- **[M] 操作方式（二选一即可验收）**
    
    - Swagger/OpenAPI 调通所有 Admin 接口（最快）
        
    - 或 Next `/admin`：最小登录页 + 表单编辑页（不追求美观）
        

**低优先级 [L]**

- **[L] slug 支持**
    
    - 手填 slug + 唯一校验（SEO 预备）
        

**Sprint 2 DoD**

- 你能登录拿 JWT → 新建草稿 → 发布 → Public 首页立刻看到。
    

---

## Sprint 3 Backlog（匿名评论 + 审核）

### 用户故事

- **[H]** 访客可提交匿名评论（默认 PENDING）
    
- **[H]** 站长可审核评论（approve/reject/delete）
    
- **[H]** 前台只展示 APPROVED
    
- **[M]** 基础防刷（不引 Redis）
    

### 技术任务

**高优先级 [H]**

- **[H] visitor_id 机制（App Router 推荐实现方式）**
    
    - 建议用 **Next Middleware**：每次请求检查 cookie，无则写入 UUID（6–12 个月）
        
    - 前端交互请求统一带 `X-Visitor-Id`（从 cookie 读出）
        
- **[H] Flyway V3：comments**
    
    - `comments(post_id, content, status, visitor_id, ip_hash, ua_hash, created_at, …)`
        
    - 索引：`(post_id,status,created_at)`、`(status,created_at desc)`
        
- **[H] MBG 生成 comments CRUD**
    
    - 自定义列表/审核查询写 `CommentMapperExt`
        
- **[H] Public 评论接口**
    
    - `POST /api/v1/posts/{postId}/comments`（写 PENDING + 指纹）
        
    - `GET /api/v1/posts/{postId}/comments`（仅 APPROVED）
        
- **[H] Admin 审核接口**
    
    - `GET /api/v1/admin/comments?status=PENDING`
        
    - `POST /api/v1/admin/comments/{id}/approve`
        
    - `POST /api/v1/admin/comments/{id}/reject`
        
    - `DELETE /api/v1/admin/comments/{id}`（或置 DELETED）
        

**中优先级 [M]**

- **[M] DB 时间窗口限流**
    
    - 同 visitor_id（或 ip_hash）在 60 秒内超过 N 条 → 429
        
- **[M] 内容规则**
    
    - 长度限制
        
    - 含链接：保持 PENDING（温和策略）或直接拒绝（强硬策略）
        

**Sprint 3 DoD**

- 访客提交评论→前台不显示→你 approve 后前台立刻显示。
    

---

## Sprint 4 Backlog（点赞 toggle + 同域上线）

### 用户故事

- **[H]** 访客可对文章/评论点赞并可取消（toggle）
    
- **[H]** 点赞防重复（visitor_id 唯一约束 + fallback）
    
- **[M]** 同域部署可访问（Nginx/Caddy 反代）
    
- **[M]** XSS/安全加固
    

### 技术任务

**高优先级 [H]**

- **[H] Flyway V4：likes（必须手写 SQL）**
    
    - `UNIQUE(target_type,target_id,visitor_id)`
        
    - **部分唯一索引**：`UNIQUE(target_type,target_id,ip_hash,ua_hash) WHERE visitor_id IS NULL`
        
- **[H] Like toggle API**
    
    - `POST /api/v1/likes/toggle`：
        
        - 先 insert；捕获 PG 唯一冲突（SQLState `23505` / Spring `DuplicateKeyException`）→ delete → 返回 liked=false
            
    - 返回 `like_count`（先实时 count，MVP OK）
        
- **[H] viewer_has_liked 输出（避免 N+1）**
    
    - 文章详情：查一次是否 liked
        
    - 评论列表：对 commentIds 批量查 liked 集合（Ext SQL）
        

**中优先级 [M]**

- **[M] 同域反代路由（关键）**
    
    - Nginx/Caddy：
        
        - `/api/` → `server:8080`
            
        - `/` → `web:3000`
            
    - 注意：Next 不使用自己的 `/api` route handlers（避免冲突与绕路）
        
- **[M] App Router 的缓存坑规避**
    
    - 动态数据（评论/点赞计数）fetch 使用 `cache: 'no-store'` 或 `revalidate: 0`
        
- **[M] XSS 加固**
    
    - Markdown 渲染 sanitize（尤其评论内容永远不可信）
        
    - 基础安全头（至少 CSP 的最小集、Referrer-Policy 等）
        

**Sprint 4 DoD**

- 点赞/取消稳定、刷新不乱；用同域部署版 Compose 在 VPS 可访问演示。
    

---

## 风险预判（结合你最终栈的“高概率坑点”）

- **Next App Router 的“服务端 fetch 相对路径不可用”**
    
    - 浏览器端可以 `/api/...`，但服务端渲染时需要绝对 URL
        
    - 规避：
        
        - 客户端交互（点赞/评论）都走相对路径（同域）
            
        - SSR/Server Component 拉数据用 `INTERNAL_API_BASE=http://server:8080`（容器网络内）
            
- **MBG 生成 + 手写 SQL 的边界不清**
    
    - 规避：CRUD 全交给 MBG；一切 join/分页/批量查询放 Ext，且给 Ext 单独评审（避免 SQL 失控）
        
- **Flyway 事务与索引语句**
    
    - 部分索引 OK，但不要用 `CREATE INDEX CONCURRENTLY`（Flyway 默认事务内会炸）
        
- **点赞 toggle 并发**
    
    - 正确姿势就是“让 DB unique 做裁判” + 捕获冲突分支；不要在应用层先查再插（TOCTOU）
        
- **同域部署下路径冲突**
    
    - `/api` 永远由 Nginx 转发到 Spring；Next 不要再实现同名 API Routes
        

---

