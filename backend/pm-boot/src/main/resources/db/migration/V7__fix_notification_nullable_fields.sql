-- ============================================================
-- V007: 修复 sys_notification 表 is_read 字段可空问题
-- ============================================================

USE `pm_business`;

-- 修复 is_read 字段，设置 NOT NULL DEFAULT 0
UPDATE `sys_notification` SET `is_read` = 0 WHERE `is_read` IS NULL;
ALTER TABLE `sys_notification` MODIFY COLUMN `is_read` TINYINT(1) NOT NULL DEFAULT 0;
