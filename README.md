# myblog

## 项目简介
- Spring Boot 3.5.9 后端项目（模块 `server`），入口类 `com.spike.blog.server.ServerApplication`
- `web/` 为预留的前端目录，当前暂无代码
- 根目录包含《Sprint 敏捷计划》《最小可行性设计文档（MVP）》等产品文档

## 目录结构
- `server/`：Spring Boot 后端源码与构建文件
- `web/`：前端占位，后续可放置 React/Vue 等实现
- `Sprint 敏捷计划.pdf`、`最小可行性设计文档（MVP）.pdf`：需求与设计资料

## 环境要求
- JDK 21
- Maven 3.9+（建议直接使用仓库自带的 `./mvnw` / `mvnw.cmd`）
- 可选：Node.js 18+（当在 `web/` 下添加前端时使用）

## 快速开始（后端）
```bash
cd server
./mvnw spring-boot:run   # Windows PowerShell 使用 .\mvnw spring-boot:run
```
应用默认运行在 8080 端口，可在 `server/src/main/resources/application.yml` 中调整。

## 构建与测试
```bash
cd server
./mvnw clean package
./mvnw test
```

## 配置与开发提示
- 配置文件：`server/src/main/resources/application.yml`（当前为空，可按环境添加数据源、端口等配置）。
- 已包含 `spring-boot-devtools`，开发模式下支持自动重启；生产构建时可用 `./mvnw -DskipTests package` 生成可执行 JAR（输出目录：`server/target/`）。
- 如需前端开发，可在 `web/` 下初始化框架，并遵守仓库根级 `.gitignore` 中的前端产物忽略规则。
