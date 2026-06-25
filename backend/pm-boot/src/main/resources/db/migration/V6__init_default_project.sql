-- ============================================================
-- V006: 初始化默认项目
-- ============================================================

USE `pm_business`;

-- 默认项目（ID 1，便于引用）
INSERT INTO `project` (`id`, `project_code`, `name`, `description`, `type`, `status`, `priority`, `project_manager_id`, `created_by`) VALUES
(1, 'PRJ-001', 'IT项目管理示范项目', '系统默认示范项目，用于功能演示和测试', 'software_dev', 'in_progress', 'high', 1, 1);

-- 将管理员添加为项目成员
INSERT INTO `project_member` (`project_id`, `user_id`, `role`, `created_by`) VALUES
(1, 1, 'project_manager', 1);
