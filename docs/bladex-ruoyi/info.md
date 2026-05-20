# BladeX-RuoYi 项目信息

## 项目概述

这是一个 monorepo，`sources/` 下包含两个独立的 Java 后端 + 前端项目：

| 项目 | 路径 | 版本 | 定位 |
|------|------|------|------|
| BladeX | `sources/bladex/` | 4.8.0.RELEASE | 企业级快速开发平台 |
| RuoYi | `sources/ruoyi/` | 3.9.2 | 若依管理系统 |

## 技术栈

### BladeX 后端
- **语言**: Java 17
- **框架**: Spring Boot 3.5.9 + Spring Cloud 2025.0.1
- **ORM**: MyBatis-Plus 3.5.14 + 动态数据源 4.3.1
- **认证**: OAuth2
- **多租户**: 字段隔离 / 数据库隔离（可选）
- **其他**: Flowable、PowerJob、LiteFlow、Sharding、Redis、OSS、SMS、Prometheus、SkyWalking
- **API 文档**: Knife4j 4.5.0
- **日志**: Log4j2 2.22.0 / Logback 1.5.18

### BladeX 前端 (saber3)
- **框架**: Vue 3.5 + Vite 5
- **UI**: Element Plus 2.10 + Avue 3.7
- **状态**: Vuex 4 + Vue Router 4

### RuoYi 后端
- **语言**: Java 17
- **框架**: Spring Boot 4.0.3
- **ORM**: MyBatis + PageHelper
- **数据库连接池**: Druid 1.2.28
- **其他**: FastJSON 2、SpringDoc、JWT、验证码、代码生成器、定时任务

### RuoYi 前端 (ruoyi-ui)
- Vue3 + Element Plus 架构（详见 `sources/ruoyi/ruoyi-vue3/ruoyi-ui/`）

## 目录结构

```
sources/
├── bladex/
│   ├── bladex-tool/          # 核心框架库（30+ 模块）
│   │   ├── blade-core-boot/   # Spring Boot 核心
│   │   ├── blade-core-auth/   # 认证核心
│   │   ├── blade-starter-*/   # 各功能 starter
│   │   └── blade-bom/         # BOM 依赖管理
│   ├── bladex-boot/          # 应用启动模块（业务入口）
│   │   ├── src/main/java/org/springblade/  # 业务代码
│   │   └── src/main/resources/             # 配置
│   ├── saber3/               # Vue3 前端
│   │   ├── src/
│   │   └── vite/
│   └── doc/
├── ruoyi/
│   └── ruoyi-vue3/
│       ├── ruoyi-admin/      # 启动模块
│       ├── ruoyi-system/     # 系统模块
│       ├── ruoyi-common/     # 通用模块
│       ├── ruoyi-framework/  # 框架模块
│       ├── ruoyi-generator/  # 代码生成
│       ├── ruoyi-quartz/     # 定时任务
│       ├── ruoyi-ui/         # 前端
│       ├── sql/              # 数据库脚本
│       └── bin/              # 脚本
```

## 常用命令

### BladeX 构建
```bash
# 构建 bladex-tool
cd sources/bladex/bladex-tool && mvn clean install

# 构建 bladex-boot
cd sources/bladex/bladex-boot && mvn clean package

# 运行 bladex-boot
cd sources/bladex/bladex-boot && mvn spring-boot:run
```

### BladeX 前端
```bash
cd sources/bladex/saber3
npm install        # 安装依赖
npm run dev        # 开发模式
npm run build      # 生产构建
```

### RuoYi 构建
```bash
cd sources/ruoyi/ruoyi-vue3 && mvn clean package
```

## 已知坑点

- `bladex-boot` 的 `pom.xml` 中排除了 `blade-core-cloud`，说明默认不使用微服务模式
- Docker 配置中使用了硬编码的 registry 地址 `192.168.0.188`，需注意环境适配
- 多租户有字段隔离和数据库隔离两种模式，按需引入对应 starter
- RuoYi 使用 Spring Boot 4.0.3（非常新版本），注意兼容性问题
