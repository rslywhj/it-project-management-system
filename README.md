# IT项目管理Web系统

支持跨组织团队协同、集团推广管理的IT项目管理平台。

## 技术栈

### 前端
- Vue 3 + TypeScript + Composition API
- Element Plus 2.x
- Pinia（状态管理）
- Vite 6（构建工具）
- ECharts 5.x（图表）
- VForm3（表单设计器）

### 后端
- Java 17 + Spring Boot 3.x
- MyBatis-Plus（ORM）
- Flowable 7.x（流程引擎）
- MySQL 8.0 + Redis + MinIO + Elasticsearch

### 架构风格
模块化单体（Modular Monolith）

## 项目结构

```
it-project-management-system/
├── frontend/                # 前端项目（Vue 3）
├── backend/                 # 后端项目（Spring Boot）
├── database/                # 数据库脚本
│   ├── V001__create_schema.sql    # 全量建表脚本
│   └── V002__init_base_data.sql   # 基础数据初始化
├── docs/                    # 文档
│   ├── product-plan.md      # 产品方案
│   ├── architecture.md      # 技术架构设计
│   └── api/                 # API 文档
└── deploy/                  # 部署配置
```

## 快速开始

### Docker 一键部署（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/rslywhj/it-project-management-system.git
cd it-project-management-system

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 修改密码等配置

# 3. 启动所有服务
make dev
# 或者直接使用 docker-compose
docker-compose up -d

# 4. 访问系统
# 前端: http://localhost
# 后端API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# MinIO: http://localhost:9001
```

### 常用命令

```bash
make dev        # 启动开发环境
make prod       # 启动生产环境
make stop       # 停止所有服务
make logs       # 查看日志
make ps         # 查看服务状态
make backup     # 备份数据库
make clean      # 清理所有容器和数据
```

### 本地开发

#### 前端
```bash
cd frontend
npm install
npm run dev
```

#### 后端
```bash
cd backend
mvn spring-boot:run
```

## 功能模块

- **P0**：项目基础管理、主计划与里程碑、需求管理、任务管理
- **P1**：交付物管理、测试管理、协同与消息、权限与组织、集团推广管理
- **P2**：看板与报表、低代码表单引擎、第三方集成
- **P3**：风险与问题管理、资源与工时、知识库

## 里程碑

- **第一期**（12-14周）：核心骨架 + 推广管理
- **第二期**（10-12周）：协同与质量 + 低代码
- **第三期**（8-10周）：深度能力与优化
