-- ============================================================
-- V004: 风险与问题管理模块建表脚本
-- ============================================================

USE `pm_business`;

-- -----------------------------------------------------------
-- 风险登记表
-- -----------------------------------------------------------
CREATE TABLE `risk` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(500) NOT NULL COMMENT '风险标题',
    `description` TEXT DEFAULT NULL COMMENT '风险描述',
    `risk_type` VARCHAR(30) DEFAULT 'technical' COMMENT '风险类型（technical/schedule/resource/requirement/external）',
    `level` VARCHAR(20) DEFAULT 'medium' COMMENT '风险等级（critical/high/medium/low）',
    `probability` VARCHAR(20) DEFAULT 'medium' COMMENT '发生概率（high/medium/low）',
    `impact` VARCHAR(20) DEFAULT 'medium' COMMENT '影响程度（high/medium/low）',
    `status` VARCHAR(30) DEFAULT 'identified' COMMENT '状态（identified/analyzing/mitigating/accepted/closed）',
    `owner_id` BIGINT DEFAULT NULL COMMENT '风险负责人ID',
    `identified_date` DATE DEFAULT NULL COMMENT '识别日期',
    `target_resolution_date` DATE DEFAULT NULL COMMENT '计划解决日期',
    `actual_resolution_date` DATE DEFAULT NULL COMMENT '实际解决日期',
    `mitigation_plan` TEXT DEFAULT NULL COMMENT '应对措施',
    `contingency_plan` TEXT DEFAULT NULL COMMENT '应急预案',
    `trigger_conditions` TEXT DEFAULT NULL COMMENT '触发条件',
    `related_requirement_id` BIGINT DEFAULT NULL COMMENT '关联需求ID',
    `related_task_id` BIGINT DEFAULT NULL COMMENT '关联任务ID',
    `attachment_path` VARCHAR(500) DEFAULT NULL COMMENT '附件路径',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_owner` (`owner_id`),
    KEY `idx_level` (`level`),
    KEY `idx_target_date` (`target_resolution_date`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险登记表';

-- -----------------------------------------------------------
-- 问题跟踪表
-- -----------------------------------------------------------
CREATE TABLE `issue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(500) NOT NULL COMMENT '问题标题',
    `description` TEXT DEFAULT NULL COMMENT '问题描述',
    `issue_type` VARCHAR(30) DEFAULT 'technical' COMMENT '问题类型（technical/process/resource/external/other）',
    `severity` VARCHAR(20) DEFAULT 'medium' COMMENT '严重程度（critical/major/minor/trivial）',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级（critical/high/medium/low）',
    `status` VARCHAR(30) DEFAULT 'open' COMMENT '状态（open/in_progress/resolved/closed/reopen）',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '指派给',
    `reported_by` BIGINT DEFAULT NULL COMMENT '报告人ID',
    `identified_date` DATE DEFAULT NULL COMMENT '发现日期',
    `target_resolution_date` DATE DEFAULT NULL COMMENT '计划解决日期',
    `actual_resolution_date` DATE DEFAULT NULL COMMENT '实际解决日期',
    `resolution` TEXT DEFAULT NULL COMMENT '解决方案',
    `root_cause` TEXT DEFAULT NULL COMMENT '根本原因',
    `impact_analysis` TEXT DEFAULT NULL COMMENT '影响分析',
    `related_risk_id` BIGINT DEFAULT NULL COMMENT '关联风险ID',
    `related_requirement_id` BIGINT DEFAULT NULL COMMENT '关联需求ID',
    `related_task_id` BIGINT DEFAULT NULL COMMENT '关联任务ID',
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
    KEY `idx_target_date` (`target_resolution_date`),
    KEY `idx_related_risk` (`related_risk_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题跟踪表';

-- -----------------------------------------------------------
-- 预警记录表
-- -----------------------------------------------------------
CREATE TABLE `risk_warning` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `warning_type` VARCHAR(30) NOT NULL COMMENT '预警类型（risk_expiry/issue_expiry/risk_escalation/issue_escalation）',
    `related_id` BIGINT NOT NULL COMMENT '关联ID（风险ID或问题ID）',
    `related_type` VARCHAR(20) NOT NULL COMMENT '关联类型（risk/issue）',
    `warning_message` TEXT DEFAULT NULL COMMENT '预警消息',
    `warning_date` DATETIME NOT NULL COMMENT '预警时间',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态（pending/acknowledged/dismissed）',
    `acknowledged_by` BIGINT DEFAULT NULL COMMENT '确认人ID',
    `acknowledged_at` DATETIME DEFAULT NULL COMMENT '确认时间',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_type` (`project_id`, `warning_type`),
    KEY `idx_related` (`related_type`, `related_id`),
    KEY `idx_status` (`status`),
    KEY `idx_warning_date` (`warning_date`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警记录表';
