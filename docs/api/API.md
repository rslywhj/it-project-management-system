# IT项目管理系统 API 契约

> 版本: 1.0.0 | 更新时间: 2026-06-04

## 概览

| 模块 | 接口数 | 说明 |
|------|--------|------|
| 认证管理 | 4 | 登录/登出/刷新Token/当前用户 |
| 项目管理 | 9 | 项目CRUD + 成员管理 |
| 需求管理 | 8 | 需求CRUD + 状态流转 + 需求池 + 指派 |
| 任务管理 | 9 | 任务CRUD + WBS + 依赖 + 进度 |
| 里程碑管理 | 5 | 里程碑CRUD |
| 推广单元管理 | 6 | 推广单元CRUD + 批量创建 |
| 推广阶段模板 | 6 | 阶段模板CRUD + 默认模板初始化 |
| 推广进度管理 | 2 | 进度查询 + 阶段推进 |
| 推广单元需求 | 5 | 差异化需求CRUD |
| 配置基线管理 | 5 | 配置基线CRUD |
| 配置差异管理 | 4 | 配置差异CRUD + 审批 |
| 推广看板 | 1 | 汇总数据 |
| **合计** | **64** | |

## 通用说明

### 认证方式
所有接口（除登录外）需要在 Header 中携带 JWT Token：
```
Authorization: Bearer <access_token>
```

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": { ... },
  "timestamp": 1717500000000
}
```

### 分页响应格式
```json
{
  "code": 200,
  "data": {
    "records": [ ... ],
    "total": 100,
    "page": 1,
    "size": 20
  }
}
```

### 错误码
| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 1001 | 用户不存在 |
| 1002 | 密码错误 |
| 1003 | 用户已禁用 |
| 1004 | Token已过期 |
| 1010 | 项目不存在 |
| 1011 | 项目编码已存在 |
| 1012 | 项目成员已存在 |
| 1020 | 需求不存在 |
| 1021 | 需求状态流转不合法 |
| 1030 | 任务不存在 |
| 1031 | 任务依赖存在循环 |
| 1040 | 里程碑不存在 |

---

## 1. 认证管理

### POST /api/auth/login
用户登录（无需认证）

**请求体**：
```json
{
  "username": "admin",
  "password": "Admin@123456"
}
```

**响应**：
```json
{
  "code": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "系统管理员",
      "role": "super_admin",
      "orgId": null
    }
  }
}
```

### POST /api/auth/refresh
刷新Token

**参数**: `refreshToken` (query)

### POST /api/auth/logout
用户登出

### GET /api/auth/me
获取当前用户信息

---

## 2. 项目管理

### POST /api/projects
创建项目

**请求体**：
```json
{
  "name": "IT项目管理系统",
  "projectCode": "PM-2026-001",
  "description": "支持跨组织团队协同的IT项目管理平台",
  "type": "software_dev",
  "priority": "high",
  "promotionEnabled": 0,
  "startDate": "2026-06-01",
  "endDate": "2026-12-31"
}
```

### GET /api/projects
项目列表（分页）

**参数**: `page`, `size`, `keyword`, `status`

### GET /api/projects/{projectId}
项目详情

### PUT /api/projects/{projectId}
更新项目

### DELETE /api/projects/{projectId}
删除项目（软删除）

### POST /api/projects/{projectId}/members
添加项目成员

**参数**: `userId`, `role` (query)

### GET /api/projects/{projectId}/members
项目成员列表

### DELETE /api/projects/{projectId}/members/{userId}
移除项目成员

---

## 3. 需求管理

### POST /api/projects/{projectId}/requirements
创建需求

**请求体**：
```json
{
  "title": "支持项目CRUD功能",
  "description": "实现项目的创建、编辑、删除、列表查询",
  "acceptanceCriteria": "可通过API完成项目全生命周期管理",
  "priority": "high",
  "source": "business",
  "category": "general",
  "assignedTo": 2,
  "estimatedHours": 40
}
```

### GET /api/projects/{projectId}/requirements
需求列表（分页）

**参数**: `page`, `size`, `keyword`, `status`, `priority`

### GET /api/requirements/{requirementId}
需求详情

### PUT /api/requirements/{requirementId}
更新需求

### PUT /api/requirements/{requirementId}/status
需求状态流转

**请求体**：
```json
{
  "targetStatus": "reviewing",
  "remark": "提交评审"
}
```

**状态流转规则**：
- `draft` → `reviewing`
- `reviewing` → `approved` | `rejected`
- `rejected` → `draft`
- `approved` → `scheduled`
- `scheduled` → `done`

### DELETE /api/requirements/{requirementId}
删除需求

### GET /api/projects/{projectId}/requirements/pool
需求池（未排期需求）

### POST /api/requirements/{requirementId}/assign
指派需求负责人

**参数**: `userId` (query)

---

## 4. 任务管理

### POST /api/projects/{projectId}/tasks
创建任务

**请求体**：
```json
{
  "title": "实现项目创建接口",
  "description": "POST /api/projects",
  "type": "dev",
  "priority": "high",
  "assignedTo": 3,
  "plannedStart": "2026-06-10",
  "plannedEnd": "2026-06-12"
}
```

### GET /api/projects/{projectId}/tasks
任务列表（分页）

**参数**: `page`, `size`, `keyword`, `status`, `assignedTo`

### GET /api/tasks/{taskId}
任务详情

### PUT /api/tasks/{taskId}
更新任务

### PUT /api/tasks/{taskId}/progress
更新任务进度

**请求体**：
```json
{
  "completionRate": 50.00,
  "remark": "接口开发完成，待测试"
}
```

**自动状态更新**：
- completionRate > 0 → status 自动变为 `in_progress`
- completionRate = 100 → status 自动变为 `done`
- 子任务进度自动汇总到父任务

### DELETE /api/tasks/{taskId}
删除任务

### POST /api/tasks/{parentTaskId}/subtasks
创建子任务（WBS分解）

### GET /api/tasks/{parentTaskId}/subtasks
获取子任务列表

### GET /api/projects/{projectId}/tasks/wbs
获取WBS完整树结构

### POST /api/tasks/{taskId}/dependencies
添加任务依赖

**请求体**：
```json
{
  "dependsOnTaskId": 5,
  "dependencyType": "FS"
}
```

**依赖类型**：
- `FS`：完成-开始（默认）
- `FF`：完成-完成
- `SS`：开始-开始
- `SF`：开始-完成

---

## 5. 里程碑管理

### POST /api/projects/{projectId}/milestones
创建里程碑

**请求体**：
```json
{
  "name": "第一期核心功能上线",
  "description": "完成项目管理、需求管理、任务管理核心功能",
  "plannedDate": "2026-09-30",
  "sortOrder": 1
}
```

### GET /api/projects/{projectId}/milestones
里程碑列表（分页）

**参数**: `page`, `size`, `status`

### GET /api/milestones/{milestoneId}
里程碑详情

### PUT /api/milestones/{milestoneId}
更新里程碑

### DELETE /api/milestones/{milestoneId}
删除里程碑

---

## 角色-权限映射

| 角色 | 权限码 | 数据范围 |
|------|--------|----------|
| super_admin | 全部 | all |
| project_admin | 39个 | project |
| project_manager | 28个 | project |
| promotion_manager | 18个 | project |
| promotion_unit_lead | 15个 | promotion_unit |
| product_manager | 12个 | project |
| developer | 14个 | self |
| tester | 11个 | project |
| external_collaborator | 13个 | self |
| guest | 9个（仅view） | self |

完整权限码清单见 `database/V002__init_base_data.sql`

---

## 6. 推广单元管理

### POST /api/projects/{projectId}/promotion-units
创建推广单元

**请求体**：
```json
{
  "orgName": "北京分公司",
  "orgCode": "BJ001",
  "responsibleUserId": 5,
  "expectedStartDate": "2026-07-01",
  "expectedEndDate": "2026-12-31",
  "remark": "备注"
}
```

### POST /api/projects/{projectId}/promotion-units/batch
批量创建推广单元

**请求体**：
```json
{
  "projectId": 1,
  "units": [
    {"orgName": "北京分公司", "orgCode": "BJ001"},
    {"orgName": "上海分公司", "orgCode": "SH001"}
  ]
}
```

### GET /api/projects/{projectId}/promotion-units
推广单元列表（分页）

**参数**: `page`, `size`, `keyword`, `status`

### GET /api/projects/{projectId}/promotion-units/{unitId}
推广单元详情

### PUT /api/projects/{projectId}/promotion-units/{unitId}
更新推广单元

### DELETE /api/projects/{projectId}/promotion-units/{unitId}
删除推广单元

---

## 7. 推广阶段模板管理

### POST /api/promotion-stage-templates
创建推广阶段模板

**请求体**：
```json
{
  "projectId": 1,
  "name": "需求调研",
  "description": "调研成员单位需求",
  "sortOrder": 1,
  "locked": false,
  "estimatedDays": 15
}
```

### GET /api/promotion-stage-templates
获取阶段模板列表

**参数**: `projectId`（可选，不传则获取集团全局模板）

### GET /api/promotion-stage-templates/{templateId}
获取阶段模板详情

### PUT /api/promotion-stage-templates/{templateId}
更新阶段模板

### DELETE /api/promotion-stage-templates/{templateId}
删除阶段模板

### POST /api/promotion-stage-templates/init-default
初始化集团默认阶段模板

---

## 8. 推广进度管理

### GET /api/promotion-units/{unitId}/progress
获取推广单元阶段进度列表

### POST /api/promotion-units/{unitId}/progress/advance
推进阶段状态

**请求体**：
```json
{
  "stageTemplateId": 1,
  "targetStatus": "in_progress",
  "completionRate": 50.00,
  "remark": "备注"
}
```

**状态流转规则**：
- `pending` → `in_progress` | `skipped`
- `in_progress` → `completed`

---

## 9. 推广单元需求管理

### POST /api/promotion-units/{unitId}/requirements
创建推广单元需求

**请求体**：
```json
{
  "title": "定制化报表需求",
  "description": "需要定制化财务报表",
  "requirementId": null,
  "type": "differential",
  "priority": "high"
}
```

### GET /api/promotion-units/{unitId}/requirements
获取推广单元需求列表（分页）

**参数**: `page`, `size`, `type`, `status`

### GET /api/promotion-units/{unitId}/requirements/{requirementId}
获取需求详情

### PUT /api/promotion-units/{unitId}/requirements/{requirementId}
更新需求

### DELETE /api/promotion-units/{unitId}/requirements/{requirementId}
删除需求

---

## 10. 配置基线管理

### POST /api/projects/{projectId}/config-baselines
创建配置基线

**请求体**：
```json
{
  "configKey": "system.theme.color",
  "configValue": "#1890ff",
  "description": "系统主题色",
  "locked": false
}
```

### GET /api/projects/{projectId}/config-baselines
获取配置基线列表（分页）

**参数**: `page`, `size`, `keyword`

### GET /api/projects/{projectId}/config-baselines/{baselineId}
获取配置基线详情

### PUT /api/projects/{projectId}/config-baselines/{baselineId}
更新配置基线

### DELETE /api/projects/{projectId}/config-baselines/{baselineId}
删除配置基线

---

## 11. 配置差异管理

### POST /api/promotion-units/{unitId}/config-diffs
创建配置差异

**请求体**：
```json
{
  "configBaselineId": 1,
  "diffValue": "#ff0000",
  "diffReason": "分公司品牌色要求"
}
```

### GET /api/promotion-units/{unitId}/config-diffs
获取配置差异列表（分页）

**参数**: `page`, `size`, `status`

### PUT /api/promotion-units/{unitId}/config-diffs/{diffId}/approve
审批配置差异

**请求体**：
```json
{
  "status": "approved",
  "remark": "同意"
}
```

### DELETE /api/promotion-units/{unitId}/config-diffs/{diffId}
删除配置差异

---

## 12. 推广看板

### GET /api/projects/{projectId}/promotion-dashboard
获取推广看板汇总数据

**响应**：
```json
{
  "code": 200,
  "data": {
    "projectId": 1,
    "projectName": "IT项目管理系统",
    "totalUnits": 10,
    "inProgressUnits": 5,
    "completedUnits": 3,
    "delayedUnits": 2,
    "overallCompletionRate": 45.50,
    "unitProgressList": [...],
    "stageCompletionList": [...]
  }
}
```
