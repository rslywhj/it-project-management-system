-- ============================================================
-- V003: 测试管理模块建表脚本
-- ============================================================

USE `pm_business`;

-- -----------------------------------------------------------
-- 测试计划表
-- -----------------------------------------------------------
CREATE TABLE `test_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `name` VARCHAR(200) NOT NULL COMMENT '计划名称',
    `description` TEXT DEFAULT NULL COMMENT '计划说明',
    `type` VARCHAR(30) DEFAULT 'functional' COMMENT '类型（functional/performance/security/regression）',
    `status` VARCHAR(30) DEFAULT 'draft' COMMENT '状态（draft/in_progress/completed/cancelled）',
    `iteration` VARCHAR(50) DEFAULT NULL COMMENT '关联迭代/版本',
    `start_date` DATE DEFAULT NULL COMMENT '计划开始日期',
    `end_date` DATE DEFAULT NULL COMMENT '计划结束日期',
    `total_cases` INT DEFAULT 0 COMMENT '用例总数',
    `executed_cases` INT DEFAULT 0 COMMENT '已执行用例数',
    `passed_cases` INT DEFAULT 0 COMMENT '通过用例数',
    `failed_cases` INT DEFAULT 0 COMMENT '失败用例数',
    `blocked_cases` INT DEFAULT 0 COMMENT '阻塞用例数',
    `pass_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '通过率',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试计划表';

-- -----------------------------------------------------------
-- 测试用例表
-- -----------------------------------------------------------
CREATE TABLE `test_case` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `module` VARCHAR(100) DEFAULT NULL COMMENT '所属模块',
    `title` VARCHAR(500) NOT NULL COMMENT '用例标题',
    `description` TEXT DEFAULT NULL COMMENT '用例描述',
    `precondition` TEXT DEFAULT NULL COMMENT '前置条件',
    `steps` TEXT DEFAULT NULL COMMENT '测试步骤（JSON格式）',
    `expected_result` TEXT DEFAULT NULL COMMENT '预期结果',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `type` VARCHAR(30) DEFAULT 'functional' COMMENT '类型（functional/api/ui/performance）',
    `status` VARCHAR(30) DEFAULT 'active' COMMENT '状态（active/deprecated/draft）',
    `requirement_id` BIGINT DEFAULT NULL COMMENT '关联需求ID',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_module` (`project_id`, `module`),
    KEY `idx_requirement` (`requirement_id`),
    KEY `idx_priority` (`priority`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试用例表';

-- -----------------------------------------------------------
-- 测试执行表
-- -----------------------------------------------------------
CREATE TABLE `test_execution` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `test_plan_id` BIGINT NOT NULL COMMENT '测试计划ID',
    `test_case_id` BIGINT NOT NULL COMMENT '测试用例ID',
    `status` VARCHAR(30) DEFAULT 'pending' COMMENT '状态（pending/passed/failed/blocked/skipped）',
    `executed_by` BIGINT DEFAULT NULL COMMENT '执行人ID',
    `executed_at` DATETIME DEFAULT NULL COMMENT '执行时间',
    `actual_result` TEXT DEFAULT NULL COMMENT '实际结果',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `bug_id` BIGINT DEFAULT NULL COMMENT '关联缺陷ID',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_plan_case` (`test_plan_id`, `test_case_id`),
    KEY `idx_case` (`test_case_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试执行表';

-- -----------------------------------------------------------
-- 缺陷表
-- -----------------------------------------------------------
CREATE TABLE `bug` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(500) NOT NULL COMMENT '缺陷标题',
    `description` TEXT DEFAULT NULL COMMENT '缺陷描述',
    `steps_to_reproduce` TEXT DEFAULT NULL COMMENT '复现步骤',
    `expected_result` TEXT DEFAULT NULL COMMENT '预期结果',
    `actual_result` TEXT DEFAULT NULL COMMENT '实际结果',
    `severity` VARCHAR(20) DEFAULT 'medium' COMMENT '严重程度（critical/major/minor/trivial）',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `status` VARCHAR(30) DEFAULT 'open' COMMENT '状态（open/in_progress/resolved/closed/reopen）',
    `type` VARCHAR(30) DEFAULT 'functional' COMMENT '类型（functional/ui/performance/security/other）',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '指派给',
    `reported_by` BIGINT DEFAULT NULL COMMENT '报告人ID',
    `test_plan_id` BIGINT DEFAULT NULL COMMENT '关联测试计划ID',
    `test_case_id` BIGINT DEFAULT NULL COMMENT '关联测试用例ID',
    `requirement_id` BIGINT DEFAULT NULL COMMENT '关联需求ID',
    `resolved_at` DATETIME DEFAULT NULL COMMENT '解决时间',
    `resolved_by` BIGINT DEFAULT NULL COMMENT '解决人ID',
    `resolution` TEXT DEFAULT NULL COMMENT '解决方案',
    `closed_at` DATETIME DEFAULT NULL COMMENT '关闭时间',
    `environment` VARCHAR(100) DEFAULT NULL COMMENT '环境信息',
    `attachment_path` VARCHAR(500) DEFAULT NULL COMMENT '附件路径',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_assigned` (`assigned_to`, `status`),
    KEY `idx_severity` (`severity`),
    KEY `idx_test_plan` (`test_plan_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缺陷表';
