-- ============================================================
-- V005: 资源分配/工时记录/知识库分类表
-- ============================================================

USE `pm_business`;

-- -----------------------------------------------------------
-- 资源分配表
-- -----------------------------------------------------------
CREATE TABLE `resource_allocation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(50) DEFAULT 'developer' COMMENT '分配角色',
    `allocation_percent` INT DEFAULT 100 COMMENT '分配百分比',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `status` VARCHAR(20) DEFAULT 'planned' COMMENT '状态（planned/active/completed/released）',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project` (`project_id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_date_range` (`start_date`, `end_date`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源分配表';

-- -----------------------------------------------------------
-- 工时记录表（timesheet，带审批流程）
-- -----------------------------------------------------------
CREATE TABLE `timesheet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `task_id` BIGINT DEFAULT NULL COMMENT '关联任务ID',
    `work_date` DATE NOT NULL COMMENT '工作日期',
    `hours` DECIMAL(5,2) NOT NULL COMMENT '工时（小时）',
    `description` TEXT DEFAULT NULL COMMENT '工作描述',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态（draft/submitted/approved/rejected）',
    `approved_by` BIGINT DEFAULT NULL COMMENT '审批人ID',
    `approved_at` DATETIME DEFAULT NULL COMMENT '审批时间',
    `reject_reason` TEXT DEFAULT NULL COMMENT '驳回原因',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project_user` (`project_id`, `user_id`),
    KEY `idx_task` (`task_id`),
    KEY `idx_work_date` (`work_date`),
    KEY `idx_status` (`status`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工时记录表';

-- -----------------------------------------------------------
-- 知识库分类表
-- -----------------------------------------------------------
CREATE TABLE `knowledge_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project_id` BIGINT DEFAULT NULL COMMENT '所属项目ID（NULL=全局）',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `updated_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
    PRIMARY KEY (`id`),
    KEY `idx_project` (`project_id`),
    KEY `idx_parent` (`parent_id`),
    KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库分类表';
