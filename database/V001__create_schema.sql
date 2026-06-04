-- ============================================================
-- IT项目管理Web系统 — 全量建表脚本
-- 版本: V001
-- 数据库: MySQL 8.0
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 创建时间: 2026-06-04
-- ============================================================

-- 创建业务数据库
CREATE DATABASE IF NOT EXISTS `pm_business` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `pm_business`;

-- ============================================================
-- 第一部分：系统基础表（sys_*）
-- ============================================================

-- -----------------------------------------------------------
-- 1.1 组织/部门表
-- -----------------------------------------------------------
CREATE TABLE `sys_org` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父组织ID',
    `org_code` VARCHAR(50) NOT NULL COMMENT '组织编码',
    `org_name` VARCHAR(200) NOT NULL COMMENT '组织名称',
    `org_type` VARCHAR(30) DEFAULT 'department' COMMENT '组织类型（company/department/team）',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（active/disabled）',
    `leader_id` BIGINT DEFAULT NULL COMMENT '负责人ID',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_org_code` (`org_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织/部门表';

-- -----------------------------------------------------------
-- 1.2 用户表
-- -----------------------------------------------------------
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `org_id` BIGINT DEFAULT NULL COMMENT '所属组织ID',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（active/disabled/locked）',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- -----------------------------------------------------------
-- 1.3 角色表
-- -----------------------------------------------------------
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '角色说明',
    `data_scope` VARCHAR(30) DEFAULT 'self' COMMENT '数据范围（all/project/promotion_unit/self）',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（active/disabled）',
    `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统内置角色（1是 0否）',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- -----------------------------------------------------------
-- 1.4 权限表
-- -----------------------------------------------------------
CREATE TABLE `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限码（如 project:view）',
    `permission_name` VARCHAR(200) NOT NULL COMMENT '权限名称',
    `module` VARCHAR(50) NOT NULL COMMENT '所属模块（project/requirement/task/milestone/delivery/test/promotion/report/system）',
    `operation` VARCHAR(30) NOT NULL COMMENT '操作类型（view/create/edit/delete/manage）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '权限说明',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- -----------------------------------------------------------
-- 1.5 角色-权限关联表
-- -----------------------------------------------------------
CREATE TABLE `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色-权限关联表';

-- -----------------------------------------------------------
-- 1.6 用户-角色关联表
-- -----------------------------------------------------------
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `project_id` BIGINT DEFAULT NULL COMMENT '项目ID（项目级角色时有值）',
    `promotion_unit_id` BIGINT DEFAULT NULL COMMENT '推广单元ID（推广单元负责人时有值）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role_project` (`user_id`, `role_id`, `project_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-角色关联表';

-- -----------------------------------------------------------
-- 1.7 数据字典表
-- -----------------------------------------------------------
CREATE TABLE `sys_dict` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
    `dict_name` VARCHAR(200) NOT NULL COMMENT '字典名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '字典说明',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（active/disabled）',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';

-- -----------------------------------------------------------
-- 1.8 字典项表
-- -----------------------------------------------------------
CREATE TABLE `sys_dict_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dict_id` BIGINT NOT NULL COMMENT '所属字典ID',
    `item_code` VARCHAR(100) NOT NULL COMMENT '字典项编码',
    `item_name` VARCHAR(200) NOT NULL COMMENT '字典项名称',
    `item_value` VARCHAR(500) DEFAULT NULL COMMENT '字典项值',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（active/disabled）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_item` (`dict_id`, `item_code`),
    KEY `idx_dict_id` (`dict_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';

-- -----------------------------------------------------------
-- 1.9 操作审计日志表
-- -----------------------------------------------------------
CREATE TABLE `sys_operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作人用户名',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '操作模块',
    `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作描述',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `request_url` VARCHAR(500) DEFAULT NULL COMMENT '请求URL',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `response_data` TEXT DEFAULT NULL COMMENT '响应数据',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    `duration` INT DEFAULT NULL COMMENT '耗时（毫秒）',
    `status` VARCHAR(20) DEFAULT 'success' COMMENT '操作状态（success/fail）',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计日志表';

-- -----------------------------------------------------------
-- 1.10 消息通知表
-- -----------------------------------------------------------
CREATE TABLE `sys_notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '接收人ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT DEFAULT NULL COMMENT '通知内容',
    `type` VARCHAR(30) DEFAULT 'system' COMMENT '通知类型（system/task/requirement/promotion/alert）',
    `related_entity_type` VARCHAR(50) DEFAULT NULL COMMENT '关联实体类型（project/task/requirement等）',
    `related_entity_id` BIGINT DEFAULT NULL COMMENT '关联实体ID',
    `is_read` TINYINT(1) DEFAULT 0 COMMENT '是否已读（0否 1是）',
    `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id_read` (`user_id`, `is_read`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';

-- -----------------------------------------------------------
-- 1.11 文件附件表
-- -----------------------------------------------------------
CREATE TABLE `file_attachment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `file_name` VARCHAR(200) NOT NULL COMMENT '原始文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径（MinIO key）',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
    `file_type` VARCHAR(100) DEFAULT NULL COMMENT '文件MIME类型',
    `file_extension` VARCHAR(20) DEFAULT NULL COMMENT '文件扩展名',
    `related_entity_type` VARCHAR(50) DEFAULT NULL COMMENT '关联实体类型',
    `related_entity_id` BIGINT DEFAULT NULL COMMENT '关联实体ID',
    `uploaded_by` BIGINT DEFAULT NULL COMMENT '上传人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_entity` (`related_entity_type`, `related_entity_id`),
    KEY `idx_uploaded_by` (`uploaded_by`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件附件表';

-- ============================================================
-- 第二部分：业务核心表
-- ============================================================

-- -----------------------------------------------------------
-- 2.1 项目表
-- -----------------------------------------------------------
CREATE TABLE `project` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_code` VARCHAR(50) NOT NULL COMMENT '项目编码',
    `name` VARCHAR(200) NOT NULL COMMENT '项目名称',
    `description` TEXT DEFAULT NULL COMMENT '项目描述',
    `type` VARCHAR(50) DEFAULT 'software_dev' COMMENT '项目类型（software_dev/system_integration/ops/other）',
    `status` VARCHAR(30) DEFAULT 'planning' COMMENT '状态（planning/in_progress/on_hold/closed）',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `promotion_enabled` TINYINT(1) DEFAULT 0 COMMENT '是否启用集团推广模块（0否 1是）',
    `start_date` DATE DEFAULT NULL COMMENT '计划开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '计划结束日期',
    `actual_start_date` DATE DEFAULT NULL COMMENT '实际开始日期',
    `actual_end_date` DATE DEFAULT NULL COMMENT '实际结束日期',
    `project_manager_id` BIGINT DEFAULT NULL COMMENT '项目负责人ID',
    `org_id` BIGINT DEFAULT NULL COMMENT '所属组织ID',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_code` (`project_code`),
    KEY `idx_status` (`status`),
    KEY `idx_project_manager` (`project_manager_id`),
    KEY `idx_org_id` (`org_id`),
    KEY `idx_type` (`type`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- -----------------------------------------------------------
-- 2.2 项目成员表
-- -----------------------------------------------------------
CREATE TABLE `project_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(30) NOT NULL DEFAULT 'member' COMMENT '项目角色（project_manager/promotion_manager/member）',
    `promotion_unit_id` BIGINT DEFAULT NULL COMMENT '关联推广单元ID（推广单元负责人时有值）',
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_user` (`project_id`, `user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_promotion_unit` (`promotion_unit_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目成员表';

-- -----------------------------------------------------------
-- 2.3 需求表
-- -----------------------------------------------------------
CREATE TABLE `requirement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(500) NOT NULL COMMENT '需求标题',
    `description` TEXT DEFAULT NULL COMMENT '需求描述',
    `acceptance_criteria` TEXT DEFAULT NULL COMMENT '验收标准',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `status` VARCHAR(30) DEFAULT 'draft' COMMENT '状态（draft/reviewing/approved/rejected/scheduled/in_progress/done）',
    `source` VARCHAR(50) DEFAULT 'business' COMMENT '来源（business/tech/regulatory）',
    `category` VARCHAR(30) DEFAULT 'general' COMMENT '分类（general通用/differential差异化）',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '负责人ID',
    `milestone_id` BIGINT DEFAULT NULL COMMENT '关联里程碑ID',
    `estimated_hours` DECIMAL(10,2) DEFAULT NULL COMMENT '预估工时（小时）',
    `actual_hours` DECIMAL(10,2) DEFAULT NULL COMMENT '实际工时（小时）',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_assigned_to` (`assigned_to`, `status`),
    KEY `idx_milestone` (`milestone_id`),
    KEY `idx_priority` (`priority`),
    KEY `idx_category` (`category`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求表';

-- -----------------------------------------------------------
-- 2.4 任务表
-- -----------------------------------------------------------
CREATE TABLE `task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `parent_task_id` BIGINT DEFAULT NULL COMMENT '父任务ID（WBS层级）',
    `requirement_id` BIGINT DEFAULT NULL COMMENT '关联需求ID',
    `promotion_unit_id` BIGINT DEFAULT NULL COMMENT '关联推广单元ID（推广任务时有值）',
    `title` VARCHAR(500) NOT NULL COMMENT '任务标题',
    `description` TEXT DEFAULT NULL COMMENT '任务描述',
    `type` VARCHAR(30) DEFAULT 'dev' COMMENT '任务类型（dev/test/deploy/training/design/review/other）',
    `status` VARCHAR(30) DEFAULT 'todo' COMMENT '状态（todo/in_progress/done/cancelled）',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '负责人ID',
    `planned_start` DATE DEFAULT NULL COMMENT '计划开始日期',
    `planned_end` DATE DEFAULT NULL COMMENT '计划结束日期',
    `actual_start` DATE DEFAULT NULL COMMENT '实际开始日期',
    `actual_end` DATE DEFAULT NULL COMMENT '实际结束日期',
    `completion_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成百分比',
    `estimated_hours` DECIMAL(10,2) DEFAULT NULL COMMENT '预估工时（小时）',
    `actual_hours` DECIMAL(10,2) DEFAULT NULL COMMENT '实际工时（小时）',
    `wbs_code` VARCHAR(50) DEFAULT NULL COMMENT 'WBS编码',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_assigned_to` (`assigned_to`, `status`),
    KEY `idx_parent_task` (`parent_task_id`),
    KEY `idx_requirement` (`requirement_id`),
    KEY `idx_promotion_unit` (`promotion_unit_id`),
    KEY `idx_wbs_code` (`wbs_code`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- -----------------------------------------------------------
-- 2.5 任务依赖表
-- -----------------------------------------------------------
CREATE TABLE `task_dependency` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `depends_on_task_id` BIGINT NOT NULL COMMENT '被依赖任务ID',
    `dependency_type` VARCHAR(10) DEFAULT 'FS' COMMENT '依赖类型（FS完成-开始/FF完成-完成/SS开始-开始/SF开始-完成）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_dependency` (`task_id`, `depends_on_task_id`),
    KEY `idx_depends_on` (`depends_on_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务依赖表';

-- -----------------------------------------------------------
-- 2.6 里程碑表
-- -----------------------------------------------------------
CREATE TABLE `milestone` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `name` VARCHAR(200) NOT NULL COMMENT '里程碑名称',
    `description` TEXT DEFAULT NULL COMMENT '里程碑说明',
    `planned_date` DATE NOT NULL COMMENT '计划日期',
    `actual_date` DATE DEFAULT NULL COMMENT '实际完成日期',
    `status` VARCHAR(30) DEFAULT 'pending' COMMENT '状态（pending/in_progress/at_risk/completed/delayed）',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_date` (`project_id`, `planned_date`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='里程碑表';

-- -----------------------------------------------------------
-- 2.7 交付物表
-- -----------------------------------------------------------
CREATE TABLE `delivery` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `milestone_id` BIGINT DEFAULT NULL COMMENT '关联里程碑ID',
    `promotion_unit_id` BIGINT DEFAULT NULL COMMENT '关联推广单元ID（推广交付物时有值）',
    `name` VARCHAR(200) NOT NULL COMMENT '交付物名称',
    `type` VARCHAR(50) DEFAULT 'document' COMMENT '类型（document/code/test_report/other）',
    `description` TEXT DEFAULT NULL COMMENT '交付物说明',
    `status` VARCHAR(30) DEFAULT 'draft' COMMENT '状态（draft/submitted/approved/rejected）',
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件存储路径',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `submitted_by` BIGINT DEFAULT NULL COMMENT '提交人ID',
    `reviewed_by` BIGINT DEFAULT NULL COMMENT '审核人ID',
    `reviewed_at` DATETIME DEFAULT NULL COMMENT '审核时间',
    `review_comment` TEXT DEFAULT NULL COMMENT '审核意见',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_milestone` (`milestone_id`),
    KEY `idx_promotion_unit` (`promotion_unit_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交付物表';

-- ============================================================
-- 第三部分：推广管理表（promotion_*）
-- ============================================================

-- -----------------------------------------------------------
-- 3.1 推广单元表
-- -----------------------------------------------------------
CREATE TABLE `promotion_unit` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `org_name` VARCHAR(200) NOT NULL COMMENT '成员单位名称',
    `org_code` VARCHAR(50) NOT NULL COMMENT '成员单位编码',
    `status` VARCHAR(30) DEFAULT 'pending' COMMENT '状态（pending/in_progress/completed/suspended）',
    `current_stage_id` BIGINT DEFAULT NULL COMMENT '当前推广阶段ID',
    `completion_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成百分比',
    `responsible_user_id` BIGINT DEFAULT NULL COMMENT '推广单元负责人ID',
    `expected_start_date` DATE DEFAULT NULL COMMENT '计划开始日期',
    `expected_end_date` DATE DEFAULT NULL COMMENT '计划结束日期',
    `actual_start_date` DATE DEFAULT NULL COMMENT '实际开始日期',
    `actual_end_date` DATE DEFAULT NULL COMMENT '实际结束日期',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_org` (`project_id`, `org_code`),
    KEY `idx_status` (`status`),
    KEY `idx_responsible_user` (`responsible_user_id`),
    KEY `idx_current_stage` (`current_stage_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推广单元表';

-- -----------------------------------------------------------
-- 3.2 推广阶段模板表
-- -----------------------------------------------------------
CREATE TABLE `promotion_stage_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT DEFAULT NULL COMMENT '所属项目ID（NULL=集团全局模板）',
    `name` VARCHAR(100) NOT NULL COMMENT '阶段名称',
    `description` TEXT DEFAULT NULL COMMENT '阶段说明',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `is_locked` TINYINT(1) DEFAULT 0 COMMENT '是否集团锁定（不可由下级跳过）',
    `estimated_days` INT DEFAULT NULL COMMENT '预计天数',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_sort` (`project_id`, `sort_order`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推广阶段模板表';

-- -----------------------------------------------------------
-- 3.3 推广单元阶段进度表
-- -----------------------------------------------------------
CREATE TABLE `promotion_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `promotion_unit_id` BIGINT NOT NULL COMMENT '推广单元ID',
    `stage_template_id` BIGINT NOT NULL COMMENT '阶段模板ID',
    `status` VARCHAR(30) DEFAULT 'pending' COMMENT '状态（pending/in_progress/completed/skipped）',
    `started_at` DATETIME DEFAULT NULL COMMENT '开始时间',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    `expected_end_date` DATE DEFAULT NULL COMMENT '预计结束日期',
    `completion_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '该阶段完成百分比',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_unit_stage` (`promotion_unit_id`, `stage_template_id`),
    KEY `idx_stage_template` (`stage_template_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推广单元阶段进度表';

-- -----------------------------------------------------------
-- 3.4 推广单元差异化需求表
-- -----------------------------------------------------------
CREATE TABLE `unit_requirement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `promotion_unit_id` BIGINT NOT NULL COMMENT '推广单元ID',
    `requirement_id` BIGINT DEFAULT NULL COMMENT '关联需求ID（NULL表示单位独立需求）',
    `title` VARCHAR(500) NOT NULL COMMENT '需求标题',
    `description` TEXT DEFAULT NULL COMMENT '需求描述',
    `type` VARCHAR(30) DEFAULT 'differential' COMMENT '类型（general通用/differential差异化）',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `status` VARCHAR(30) DEFAULT 'draft' COMMENT '状态（draft/reviewing/approved/scheduled/done）',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_unit_id` (`promotion_unit_id`),
    KEY `idx_requirement` (`requirement_id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推广单元差异化需求表';

-- -----------------------------------------------------------
-- 3.5 配置基线表
-- -----------------------------------------------------------
CREATE TABLE `config_baseline` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `config_key` VARCHAR(200) NOT NULL COMMENT '配置项键',
    `config_value` TEXT DEFAULT NULL COMMENT '标准配置值',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '配置说明',
    `is_locked` TINYINT(1) DEFAULT 0 COMMENT '是否锁定（不可由下级覆盖）',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_project_key` (`project_id`, `config_key`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配置基线表';

-- -----------------------------------------------------------
-- 3.6 成员单位配置差异表
-- -----------------------------------------------------------
CREATE TABLE `unit_config_diff` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `promotion_unit_id` BIGINT NOT NULL COMMENT '推广单元ID',
    `config_baseline_id` BIGINT NOT NULL COMMENT '关联配置基线ID',
    `diff_value` TEXT DEFAULT NULL COMMENT '差异值',
    `diff_reason` TEXT DEFAULT NULL COMMENT '差异原因',
    `status` VARCHAR(30) DEFAULT 'pending' COMMENT '状态（pending/approved/rejected）',
    `approved_by` BIGINT DEFAULT NULL COMMENT '审批人ID',
    `approved_at` DATETIME DEFAULT NULL COMMENT '审批时间',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_unit_id` (`promotion_unit_id`),
    KEY `idx_config_baseline` (`config_baseline_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成员单位配置差异表';

-- ============================================================
-- 第四部分：低代码引擎表（form_* / process_*）
-- ============================================================

-- -----------------------------------------------------------
-- 4.1 表单定义表
-- -----------------------------------------------------------
CREATE TABLE `form_definition` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `form_key` VARCHAR(100) NOT NULL COMMENT '表单标识',
    `name` VARCHAR(200) NOT NULL COMMENT '表单名称',
    `schema_json` TEXT NOT NULL COMMENT 'VForm3生成的JSON Schema',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `bindable_entity` VARCHAR(100) DEFAULT NULL COMMENT '绑定的业务实体类型',
    `project_id` BIGINT DEFAULT NULL COMMENT '所属项目ID（NULL=全局模板）',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（draft/active/deprecated）',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_form_key_version` (`form_key`, `version`),
    KEY `idx_project` (`project_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单定义表';

-- -----------------------------------------------------------
-- 4.2 表单实例数据表
-- -----------------------------------------------------------
CREATE TABLE `form_instance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `form_definition_id` BIGINT NOT NULL COMMENT '关联表单定义ID',
    `entity_id` BIGINT DEFAULT NULL COMMENT '关联业务实体ID',
    `entity_type` VARCHAR(50) DEFAULT NULL COMMENT '业务实体类型',
    `data_json` TEXT DEFAULT NULL COMMENT '用户填写的表单数据（JSON格式）',
    `process_instance_id` VARCHAR(64) DEFAULT NULL COMMENT '关联Flowable流程实例ID',
    `task_id` VARCHAR(64) DEFAULT NULL COMMENT '关联Flowable任务ID',
    `submitted_by` BIGINT DEFAULT NULL COMMENT '提交人ID',
    `submitted_at` DATETIME DEFAULT NULL COMMENT '提交时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_entity` (`entity_type`, `entity_id`),
    KEY `idx_process` (`process_instance_id`),
    KEY `idx_form_definition` (`form_definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='表单实例数据表';

-- -----------------------------------------------------------
-- 4.3 流程实例与业务实体关联表
-- -----------------------------------------------------------
CREATE TABLE `process_instance_ref` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `process_instance_id` VARCHAR(64) NOT NULL COMMENT 'Flowable流程实例ID',
    `entity_id` BIGINT NOT NULL COMMENT '业务实体ID',
    `entity_type` VARCHAR(50) NOT NULL COMMENT '业务实体类型',
    `status` VARCHAR(30) DEFAULT 'running' COMMENT '状态（running/completed/cancelled）',
    `started_by` BIGINT DEFAULT NULL COMMENT '发起人ID',
    `started_at` DATETIME DEFAULT NULL COMMENT '发起时间',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_process_instance` (`process_instance_id`),
    KEY `idx_entity` (`entity_type`, `entity_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例与业务实体关联表';

-- ============================================================
-- 第五部分：外键约束（逻辑外键，不强制，由应用层保证）
-- ============================================================
-- 说明：本系统采用逻辑外键设计，不创建数据库级外键约束，
-- 以避免级联操作带来的性能问题和锁表风险。
-- 数据完整性由应用层（Service层事务）保证。
-- 以下为逻辑关系记录，供开发参考：
--
-- sys_user.org_id           → sys_org.id
-- sys_role_permission.role_id → sys_role.id
-- sys_role_permission.permission_id → sys_permission.id
-- sys_user_role.user_id     → sys_user.id
-- sys_user_role.role_id     → sys_role.id
-- sys_user_role.project_id  → project.id
-- sys_user_role.promotion_unit_id → promotion_unit.id
-- sys_dict_item.dict_id     → sys_dict.id
-- project.project_manager_id → sys_user.id
-- project.org_id            → sys_org.id
-- project_member.project_id → project.id
-- project_member.user_id    → sys_user.id
-- requirement.project_id    → project.id
-- requirement.assigned_to   → sys_user.id
-- requirement.milestone_id  → milestone.id
-- task.project_id           → project.id
-- task.parent_task_id       → task.id
-- task.requirement_id       → requirement.id
-- task.promotion_unit_id    → promotion_unit.id
-- task.assigned_to          → sys_user.id
-- task_dependency.task_id   → task.id
-- task_dependency.depends_on_task_id → task.id
-- milestone.project_id      → project.id
-- delivery.project_id       → project.id
-- delivery.milestone_id     → milestone.id
-- delivery.promotion_unit_id → promotion_unit.id
-- promotion_unit.project_id → project.id
-- promotion_unit.responsible_user_id → sys_user.id
-- promotion_stage_template.project_id → project.id
-- promotion_progress.promotion_unit_id → promotion_unit.id
-- promotion_progress.stage_template_id → promotion_stage_template.id
-- unit_requirement.promotion_unit_id → promotion_unit.id
-- unit_requirement.requirement_id → requirement.id
-- config_baseline.project_id → project.id
-- unit_config_diff.promotion_unit_id → promotion_unit.id
-- unit_config_diff.config_baseline_id → config_baseline.id
-- form_definition.project_id → project.id
-- form_instance.form_definition_id → form_definition.id
-- ============================================================

-- ============================================================
-- 第六部分：Flowable 独立数据库
-- ============================================================

-- 创建Flowable独立数据库
CREATE DATABASE IF NOT EXISTS `pm_flowable` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- Flowable引擎启动时会自动创建 ACT_* 表，无需手动建表

-- ============================================================
-- 建表完成
-- ============================================================
