-- ============================================================
-- V004: 风险/资源/知识库模块建表脚本
-- ============================================================

USE `pm_business`;

-- -----------------------------------------------------------
-- 风险表
-- -----------------------------------------------------------
CREATE TABLE `risk` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(500) NOT NULL COMMENT '风险标题',
    `description` TEXT DEFAULT NULL COMMENT '风险描述',
    `category` VARCHAR(30) DEFAULT 'technical' COMMENT '类别（technical/resource/schedule/quality/external）',
    `probability` VARCHAR(20) DEFAULT 'medium' COMMENT '发生概率（high/medium/low）',
    `impact` VARCHAR(20) DEFAULT 'medium' COMMENT '影响程度（high/medium/low）',
    `risk_level` VARCHAR(20) DEFAULT 'medium' COMMENT '风险等级（critical/high/medium/low）',
    `status` VARCHAR(30) DEFAULT 'open' COMMENT '状态（open/monitoring/mitigated/closed/realized）',
    `owner_id` BIGINT DEFAULT NULL COMMENT '风险负责人ID',
    `mitigation_plan` TEXT DEFAULT NULL COMMENT '应对措施',
    `contingency_plan` TEXT DEFAULT NULL COMMENT '应急预案',
    `identified_date` DATE DEFAULT NULL COMMENT '识别日期',
    `review_date` DATE DEFAULT NULL COMMENT '复审日期',
    `closed_date` DATE DEFAULT NULL COMMENT '关闭日期',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_owner` (`owner_id`),
    KEY `idx_risk_level` (`risk_level`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风险表';

-- -----------------------------------------------------------
-- 问题表
-- -----------------------------------------------------------
CREATE TABLE `issue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(500) NOT NULL COMMENT '问题标题',
    `description` TEXT DEFAULT NULL COMMENT '问题描述',
    `category` VARCHAR(30) DEFAULT 'technical' COMMENT '类别（technical/process/resource/communication/other）',
    `severity` VARCHAR(20) DEFAULT 'medium' COMMENT '严重程度（critical/major/minor/trivial）',
    `status` VARCHAR(30) DEFAULT 'open' COMMENT '状态（open/in_progress/resolved/closed/reopen）',
    `assigned_to` BIGINT DEFAULT NULL COMMENT '指派给',
    `reported_by` BIGINT DEFAULT NULL COMMENT '报告人ID',
    `resolution` TEXT DEFAULT NULL COMMENT '解决方案',
    `resolved_at` DATETIME DEFAULT NULL COMMENT '解决时间',
    `resolved_by` BIGINT DEFAULT NULL COMMENT '解决人ID',
    `closed_at` DATETIME DEFAULT NULL COMMENT '关闭时间',
    `due_date` DATE DEFAULT NULL COMMENT '期望解决日期',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_status` (`project_id`, `status`),
    KEY `idx_assigned` (`assigned_to`, `status`),
    KEY `idx_severity` (`severity`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问题表';

-- -----------------------------------------------------------
-- 资源表
-- -----------------------------------------------------------
CREATE TABLE `resource` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `project_id` BIGINT DEFAULT NULL COMMENT '所属项目ID',
    `skill_tags` VARCHAR(500) DEFAULT NULL COMMENT '技能标签（逗号分隔）',
    `availability` VARCHAR(20) DEFAULT 'available' COMMENT '可用状态（available/busy/unavailable/on_leave）',
    `workload_percent` INT DEFAULT 0 COMMENT '当前工作负载百分比',
    `capacity_hours_per_week` DECIMAL(5,1) DEFAULT 40.0 COMMENT '每周可用工时',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_project` (`user_id`, `project_id`),
    KEY `idx_project` (`project_id`),
    KEY `idx_availability` (`availability`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源表';

-- -----------------------------------------------------------
-- 工时记录表
-- -----------------------------------------------------------
CREATE TABLE `work_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `task_id` BIGINT DEFAULT NULL COMMENT '关联任务ID',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `hours` DECIMAL(5,2) NOT NULL COMMENT '工时（小时）',
    `work_type` VARCHAR(30) DEFAULT 'development' COMMENT '工作类型（development/testing/meeting/design/review/other）',
    `description` TEXT DEFAULT NULL COMMENT '工作描述',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_user` (`project_id`, `user_id`),
    KEY `idx_task` (`task_id`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工时记录表';

-- -----------------------------------------------------------
-- 知识库文章表
-- -----------------------------------------------------------
CREATE TABLE `knowledge_article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT DEFAULT NULL COMMENT '所属项目ID（NULL=全局）',
    `title` VARCHAR(500) NOT NULL COMMENT '文章标题',
    `content` LONGTEXT DEFAULT NULL COMMENT '文章内容',
    `category` VARCHAR(50) DEFAULT 'experience' COMMENT '类别（experience/best_practice/lesson_learned/template/guide）',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态（draft/published/archived）',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `author_id` BIGINT DEFAULT NULL COMMENT '作者ID',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project` (`project_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_author` (`author_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文章表';

-- -----------------------------------------------------------
-- 模板表
-- -----------------------------------------------------------
CREATE TABLE `template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name` VARCHAR(200) NOT NULL COMMENT '模板名称',
    `description` TEXT DEFAULT NULL COMMENT '模板说明',
    `type` VARCHAR(30) DEFAULT 'project' COMMENT '类型（project/document/task/requirement）',
    `content` LONGTEXT DEFAULT NULL COMMENT '模板内容（JSON格式）',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态（active/deprecated）',
    `is_system` TINYINT(1) DEFAULT 0 COMMENT '是否系统内置',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模板表';
