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
├── docs/                    # 文档
│   ├── product-plan.md      # 产品方案
│   ├── architecture.md      # 技术架构设计
│   └── api/                 # API 文档
└── deploy/                  # 部署配置
```

## 快速开始

### 前端
```bash
cd frontend
npm install
npm run dev
```

### 后端
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
