-- ============================================================
-- IT项目管理Web系统 — 基础数据初始化脚本
-- 版本: V002
-- 数据库: MySQL 8.0
-- 创建时间: 2026-06-04
-- ============================================================

USE `pm_business`;

-- ============================================================
-- 第一部分：角色初始化（10个角色）
-- ============================================================

INSERT INTO `sys_role` (`id`, `role_code`, `role_name`, `description`, `data_scope`, `sort_order`, `status`, `is_system`) VALUES
(1,  'super_admin',           '系统管理员',   '平台级管理，全局配置、用户管理',                              'all',            1,  'active', 1),
(2,  'project_admin',         '项目管理员',   '项目级管理，配置、模板管理',                                 'project',        2,  'active', 1),
(3,  'project_manager',       '项目经理',     '项目执行管理，计划编排、需求管理',                             'project',        3,  'active', 1),
(4,  'promotion_manager',     '推广管理员',   '集团推广类项目专用，推广进度监控',                             'project',        4,  'active', 1),
(5,  'promotion_unit_lead',   '推广单元负责人','成员单位推广执行',                                        'promotion_unit', 5,  'active', 1),
(6,  'product_manager',       '产品经理',     '需求管理',                                              'project',        6,  'active', 1),
(7,  'developer',             '开发人员',     '任务执行',                                              'self',           7,  'active', 1),
(8,  'tester',                '测试人员',     '测试执行',                                              'project',        8,  'active', 1),
(9,  'external_collaborator', '外部协作者',   '跨组织/外包，受限访问',                                     'self',           9,  'active', 1),
(10, 'guest',                 '访客',         '只读查看',                                              'self',           10, 'active', 1);

-- ============================================================
-- 第二部分：权限初始化（54个权限码）
-- ============================================================

INSERT INTO `sys_permission` (`id`, `permission_code`, `permission_name`, `module`, `operation`, `description`, `sort_order`) VALUES
-- 项目管理（project）
(1,  'project:view',    '查看项目',       'project',    'view',    '查看项目列表和详情',        1),
(2,  'project:create',  '创建项目',       'project',    'create',  '创建新项目',              2),
(3,  'project:edit',    '编辑项目',       'project',    'edit',    '编辑项目基本信息',          3),
(4,  'project:delete',  '删除项目',       'project',    'delete',  '删除项目',               4),
(5,  'project:manage',  '管理项目',       'project',    'manage',  '项目配置、模板管理',         5),

-- 团队管理（team）
(6,  'team:view',       '查看团队',       'team',       'view',    '查看项目成员',             6),
(7,  'team:create',     '添加成员',       'team',       'create',  '添加项目成员',             7),
(8,  'team:edit',       '编辑成员',       'team',       'edit',    '修改成员角色',             8),
(9,  'team:delete',     '移除成员',       'team',       'delete',  '移除成员',               9),

-- 需求管理（requirement）
(10, 'requirement:view',    '查看需求',    'requirement','view',    '查看需求列表和详情',         10),
(11, 'requirement:create',  '创建需求',    'requirement','create',  '创建需求',               11),
(12, 'requirement:edit',    '编辑需求',    'requirement','edit',    '编辑需求',               12),
(13, 'requirement:delete',  '删除需求',    'requirement','delete',  '删除需求',               13),
(14, 'requirement:manage',  '管理需求',    'requirement','manage',  '需求评审、排期、状态流转管理',    14),

-- 任务管理（task）
(15, 'task:view',       '查看任务',       'task',       'view',    '查看任务列表和详情',         15),
(16, 'task:create',     '创建任务',       'task',       'create',  '创建任务',               16),
(17, 'task:edit',       '编辑任务',       'task',       'edit',    '编辑任务',               17),
(18, 'task:delete',     '删除任务',       'task',       'delete',  '删除任务',               18),
(19, 'task:manage',     '管理任务',       'task',       'manage',  'WBS分解、任务分配、批量操作',    19),

-- 里程碑管理（milestone）
(20, 'milestone:view',    '查看里程碑',    'milestone',  'view',    '查看里程碑',              20),
(21, 'milestone:create',  '创建里程碑',    'milestone',  'create',  '创建里程碑',              21),
(22, 'milestone:edit',    '编辑里程碑',    'milestone',  'edit',    '编辑里程碑',              22),
(23, 'milestone:delete',  '删除里程碑',    'milestone',  'delete',  '删除里程碑',              23),

-- 交付物管理（delivery）
(24, 'delivery:view',    '查看交付物',     'delivery',   'view',    '查看交付物',              24),
(25, 'delivery:create',  '提交交付物',     'delivery',   'create',  '提交交付物',              25),
(26, 'delivery:edit',    '编辑交付物',     'delivery',   'edit',    '编辑交付物',              26),
(27, 'delivery:delete',  '删除交付物',     'delivery',   'delete',  '删除交付物',              27),
(28, 'delivery:manage',  '审核交付物',     'delivery',   'manage',  '审核交付物',              28),

-- 测试管理（test）
(29, 'test:view',       '查看测试',       'test',       'view',    '查看测试计划、用例、缺陷',      29),
(30, 'test:create',     '创建测试',       'test',       'create',  '创建测试计划、用例、缺陷',      30),
(31, 'test:edit',       '编辑测试',       'test',       'edit',    '编辑测试内容',             31),
(32, 'test:delete',     '删除测试',       'test',       'delete',  '删除测试内容',             32),
(33, 'test:manage',     '管理测试',       'test',       'manage',  '测试报告、测试配置管理',        33),

-- 推广管理（promotion）
(34, 'promotion:view',    '查看推广',      'promotion',  'view',    '查看推广单元和进度',          34),
(35, 'promotion:create',  '创建推广',      'promotion',  'create',  '创建推广单元',             35),
(36, 'promotion:edit',    '编辑推广',      'promotion',  'edit',    '编辑推广单元',             36),
(37, 'promotion:delete',  '删除推广',      'promotion',  'delete',  '删除推广单元',             37),
(38, 'promotion:manage',  '管理推广',      'promotion',  'manage',  '推广阶段模板、批量任务、集团级报表', 38),

-- 报表（report）
(39, 'report:view',      '查看报表',       'report',     'view',    '查看项目看板、报表',          39),
(40, 'report:manage',    '管理报表',       'report',     'manage',  '自定义报表配置',            40),

-- 系统管理（system）
(41, 'system:user',      '用户管理',       'system',     'manage',  '用户管理',               41),
(42, 'system:role',      '角色管理',       'system',     'manage',  '角色管理',               42),
(43, 'system:permission','权限管理',       'system',     'manage',  '权限管理',               43),
(44, 'system:dict',      '字典管理',       'system',     'manage',  '数据字典管理',             44),
(45, 'system:org',       '组织管理',       'system',     'manage',  '组织管理',               45),
(46, 'system:log',       '日志查看',       'system',     'view',    '操作日志查看',             46),
-- 风险管理（risk）
(47, 'risk:view',        '查看风险',       'risk',       'view',    '查看风险列表和详情',        47),
(48, 'risk:create',      '创建风险',       'risk',       'create',  '创建风险',               48),
(49, 'risk:edit',        '编辑风险',       'risk',       'edit',    '编辑风险',               49),
(50, 'risk:delete',      '删除风险',       'risk',       'delete',  '删除风险',               50),
-- 资源管理（resource）
(51, 'resource:view',    '查看资源',       'resource',   'view',    '查看资源和工时',            51),
(52, 'resource:create',  '创建资源',       'resource',   'create',  '创建资源',               52),
(55, 'resource:edit',    '编辑资源',       'resource',   'edit',    '编辑资源',               55),
(56, 'resource:delete',  '删除资源',       'resource',   'delete',  '删除资源',               56),
-- 知识库（knowledge）
(53, 'knowledge:view',   '查看知识库',     'knowledge',  'view',    '查看知识库文章',            53),
(54, 'knowledge:create', '创建知识库',     'knowledge',  'create',  '创建知识库文章和模板',       54),
(57, 'knowledge:edit',   '编辑知识库',     'knowledge',  'edit',    '编辑知识库文章和模板',       57),
(58, 'knowledge:delete', '删除知识库',     'knowledge',  'delete',  '删除知识库文章和模板',       58);

-- ============================================================
-- 第三部分：角色-权限映射
-- ============================================================

-- 临时变量：角色ID
SET @role_super_admin        = 1;
SET @role_project_admin      = 2;
SET @role_project_manager    = 3;
SET @role_promotion_manager  = 4;
SET @role_promotion_unit_lead = 5;
SET @role_product_manager    = 6;
SET @role_developer          = 7;
SET @role_tester             = 8;
SET @role_external           = 9;
SET @role_guest              = 10;

-- -----------------------------------------------------------
-- 超级管理员（role_id=1）：全部权限
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT @role_super_admin, `id` FROM `sys_permission`;

-- -----------------------------------------------------------
-- 项目管理员（role_id=2）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view/create/edit/delete/manage
(@role_project_admin, 1), (@role_project_admin, 2), (@role_project_admin, 3), (@role_project_admin, 4), (@role_project_admin, 5),
-- team: view/create/edit/delete
(@role_project_admin, 6), (@role_project_admin, 7), (@role_project_admin, 8), (@role_project_admin, 9),
-- requirement: view/create/edit/delete/manage
(@role_project_admin, 10), (@role_project_admin, 11), (@role_project_admin, 12), (@role_project_admin, 13), (@role_project_admin, 14),
-- task: view/create/edit/delete/manage
(@role_project_admin, 15), (@role_project_admin, 16), (@role_project_admin, 17), (@role_project_admin, 18), (@role_project_admin, 19),
-- milestone: view/create/edit/delete
(@role_project_admin, 20), (@role_project_admin, 21), (@role_project_admin, 22), (@role_project_admin, 23),
-- delivery: view/create/edit/delete/manage
(@role_project_admin, 24), (@role_project_admin, 25), (@role_project_admin, 26), (@role_project_admin, 27), (@role_project_admin, 28),
-- test: view/create/edit/delete/manage
(@role_project_admin, 29), (@role_project_admin, 30), (@role_project_admin, 31), (@role_project_admin, 32), (@role_project_admin, 33),
-- promotion: view/create/edit/delete/manage
(@role_project_admin, 34), (@role_project_admin, 35), (@role_project_admin, 36), (@role_project_admin, 37), (@role_project_admin, 38),
-- report: view/manage
(@role_project_admin, 39), (@role_project_admin, 40),
-- risk: view/create/edit/delete
(@role_project_admin, 47), (@role_project_admin, 48), (@role_project_admin, 49), (@role_project_admin, 50),
-- resource: view/create/edit/delete
(@role_project_admin, 51), (@role_project_admin, 52), (@role_project_admin, 55), (@role_project_admin, 56),
-- knowledge: view/create/edit/delete
(@role_project_admin, 53), (@role_project_admin, 54), (@role_project_admin, 57), (@role_project_admin, 58);

-- -----------------------------------------------------------
-- 项目经理（role_id=3）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view/create/edit
(@role_project_manager, 1), (@role_project_manager, 2), (@role_project_manager, 3),
-- team: view/create/edit
(@role_project_manager, 6), (@role_project_manager, 7), (@role_project_manager, 8),
-- requirement: view/create/edit/delete/manage
(@role_project_manager, 10), (@role_project_manager, 11), (@role_project_manager, 12), (@role_project_manager, 13), (@role_project_manager, 14),
-- task: view/create/edit/delete/manage
(@role_project_manager, 15), (@role_project_manager, 16), (@role_project_manager, 17), (@role_project_manager, 18), (@role_project_manager, 19),
-- milestone: view/create/edit/delete
(@role_project_manager, 20), (@role_project_manager, 21), (@role_project_manager, 22), (@role_project_manager, 23),
-- delivery: view/create/edit/delete/manage
(@role_project_manager, 24), (@role_project_manager, 25), (@role_project_manager, 26), (@role_project_manager, 27), (@role_project_manager, 28),
-- test: view/create/edit/delete
(@role_project_manager, 29), (@role_project_manager, 30), (@role_project_manager, 31), (@role_project_manager, 32),
-- promotion: view/create/edit
(@role_project_manager, 34), (@role_project_manager, 35), (@role_project_manager, 36),
-- report: view
(@role_project_manager, 39),
-- risk: view/create/edit
(@role_project_manager, 47), (@role_project_manager, 48), (@role_project_manager, 49),
-- resource: view/create
(@role_project_manager, 51), (@role_project_manager, 52),
-- knowledge: view/create
(@role_project_manager, 53), (@role_project_manager, 54);

-- -----------------------------------------------------------
-- 推广管理员（role_id=4）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view
(@role_promotion_manager, 1),
-- team: view
(@role_promotion_manager, 6),
-- requirement: view/create
(@role_promotion_manager, 10), (@role_promotion_manager, 11),
-- task: view
(@role_promotion_manager, 15),
-- milestone: view
(@role_promotion_manager, 20),
-- delivery: view
(@role_promotion_manager, 24),
-- test: view
(@role_promotion_manager, 29),
-- promotion: view/create/edit/delete/manage
(@role_promotion_manager, 34), (@role_promotion_manager, 35), (@role_promotion_manager, 36), (@role_promotion_manager, 37), (@role_promotion_manager, 38),
-- report: view
(@role_promotion_manager, 39),
-- risk: view
(@role_promotion_manager, 47),
-- knowledge: view
(@role_promotion_manager, 53);

-- -----------------------------------------------------------
-- 推广单元负责人（role_id=5）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view
(@role_promotion_unit_lead, 1),
-- team: view
(@role_promotion_unit_lead, 6),
-- requirement: view/create
(@role_promotion_unit_lead, 10), (@role_promotion_unit_lead, 11),
-- task: view/create/edit
(@role_promotion_unit_lead, 15), (@role_promotion_unit_lead, 16), (@role_promotion_unit_lead, 17),
-- milestone: view
(@role_promotion_unit_lead, 20),
-- delivery: view/create/edit
(@role_promotion_unit_lead, 24), (@role_promotion_unit_lead, 25), (@role_promotion_unit_lead, 26),
-- test: view
(@role_promotion_unit_lead, 29),
-- promotion: view/edit（本单元）
(@role_promotion_unit_lead, 34), (@role_promotion_unit_lead, 36),
-- report: view
(@role_promotion_unit_lead, 39),
-- risk: view
(@role_promotion_unit_lead, 47),
-- knowledge: view
(@role_promotion_unit_lead, 53);

-- -----------------------------------------------------------
-- 产品经理（role_id=6）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view
(@role_product_manager, 1),
-- team: view
(@role_product_manager, 6),
-- requirement: view/create/edit/manage
(@role_product_manager, 10), (@role_product_manager, 11), (@role_product_manager, 12), (@role_product_manager, 14),
-- task: view
(@role_product_manager, 15),
-- milestone: view
(@role_product_manager, 20),
-- delivery: view
(@role_product_manager, 24),
-- test: view
(@role_product_manager, 29),
-- promotion: view
(@role_product_manager, 34),
-- report: view
(@role_product_manager, 39),
-- risk: view
(@role_product_manager, 47),
-- knowledge: view
(@role_product_manager, 53);

-- -----------------------------------------------------------
-- 开发人员（role_id=7）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view
(@role_developer, 1),
-- team: view
(@role_developer, 6),
-- requirement: view
(@role_developer, 10),
-- task: view/create/edit
(@role_developer, 15), (@role_developer, 16), (@role_developer, 17),
-- milestone: view
(@role_developer, 20),
-- delivery: view/create/edit
(@role_developer, 24), (@role_developer, 25), (@role_developer, 26),
-- test: view/create/edit
(@role_developer, 29), (@role_developer, 30), (@role_developer, 31),
-- promotion: view
(@role_developer, 34),
-- report: view
(@role_developer, 39),
-- risk: view
(@role_developer, 47),
-- resource: view/create（开发人员可填报工时）
(@role_developer, 51), (@role_developer, 52),
-- knowledge: view/create（开发人员可贡献知识库）
(@role_developer, 53), (@role_developer, 54);

-- -----------------------------------------------------------
-- 测试人员（role_id=8）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view
(@role_tester, 1),
-- team: view
(@role_tester, 6),
-- requirement: view
(@role_tester, 10),
-- task: view
(@role_tester, 15),
-- milestone: view
(@role_tester, 20),
-- delivery: view
(@role_tester, 24),
-- test: view/create/edit
(@role_tester, 29), (@role_tester, 30), (@role_tester, 31),
-- promotion: view
(@role_tester, 34),
-- report: view
(@role_tester, 39),
-- risk: view
(@role_tester, 47),
-- knowledge: view
(@role_tester, 53);

-- -----------------------------------------------------------
-- 外部协作者（role_id=9）
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- project: view
(@role_external, 1),
-- team: view
(@role_external, 6),
-- requirement: view
(@role_external, 10),
-- task: view/edit
(@role_external, 15), (@role_external, 17),
-- milestone: view
(@role_external, 20),
-- delivery: view/create/edit
(@role_external, 24), (@role_external, 25), (@role_external, 26),
-- test: view/create/edit
(@role_external, 29), (@role_external, 30), (@role_external, 31),
-- report: view
(@role_external, 39),
-- risk: view
(@role_external, 47),
-- knowledge: view
(@role_external, 53);

-- -----------------------------------------------------------
-- 访客（role_id=10）：仅view权限
-- -----------------------------------------------------------
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
(@role_guest, 1),   -- project:view
(@role_guest, 6),   -- team:view
(@role_guest, 10),  -- requirement:view
(@role_guest, 15),  -- task:view
(@role_guest, 20),  -- milestone:view
(@role_guest, 24),  -- delivery:view
(@role_guest, 29),  -- test:view
(@role_guest, 34),  -- promotion:view
(@role_guest, 39),  -- report:view
(@role_guest, 47),  -- risk:view
(@role_guest, 53);  -- knowledge:view

-- ============================================================
-- 第四部分：超级管理员账号初始化
-- ============================================================

-- 默认管理员账号：admin / Admin@123456（BCrypt加密）
-- 请在首次登录后立即修改密码
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `email`, `status`, `created_by`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'admin@pm-system.com', 'active', 1);

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, @role_super_admin);

-- ============================================================
-- 第五部分：数据字典初始化
-- ============================================================

-- -----------------------------------------------------------
-- 5.1 项目类型字典
-- -----------------------------------------------------------
INSERT INTO `sys_dict` (`id`, `dict_code`, `dict_name`, `description`) VALUES
(1,  'project_type',       '项目类型',     '项目类型枚举'),
(2,  'project_status',     '项目状态',     '项目状态枚举'),
(3,  'priority',           '优先级',       '通用优先级枚举'),
(4,  'requirement_status', '需求状态',     '需求状态枚举'),
(5,  'requirement_source', '需求来源',     '需求来源枚举'),
(6,  'requirement_category','需求分类',    '需求分类枚举'),
(7,  'task_type',          '任务类型',     '任务类型枚举'),
(8,  'task_status',        '任务状态',     '任务状态枚举'),
(9,  'milestone_status',   '里程碑状态',   '里程碑状态枚举'),
(10, 'delivery_status',    '交付物状态',   '交付物状态枚举'),
(11, 'promotion_unit_status','推广单元状态','推广单元状态枚举'),
(12, 'promotion_progress_status','推广进度状态','推广阶段进度状态枚举'),
(13, 'user_status',        '用户状态',     '用户状态枚举'),
(14, 'org_type',           '组织类型',     '组织类型枚举'),
(15, 'dependency_type',    '依赖类型',     '任务依赖类型枚举');

-- -----------------------------------------------------------
-- 5.2 字典项初始化
-- -----------------------------------------------------------

-- 项目类型
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(1, 'software_dev',     '软件开发',   'software_dev',     1),
(1, 'system_integration','系统集成',  'system_integration', 2),
(1, 'ops',              '运维项目',   'ops',              3),
(1, 'other',            '其他',      'other',            4);

-- 项目状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(2, 'planning',    '规划中',   'planning',    1),
(2, 'in_progress', '进行中',   'in_progress', 2),
(2, 'on_hold',     '已暂停',   'on_hold',     3),
(2, 'closed',      '已关闭',   'closed',      4);

-- 优先级
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(3, 'critical', '紧急', 'critical', 1),
(3, 'high',     '高',  'high',     2),
(3, 'medium',   '中',  'medium',   3),
(3, 'low',      '低',  'low',      4);

-- 需求状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(4, 'draft',      '草稿',    'draft',      1),
(4, 'reviewing',  '评审中',   'reviewing',  2),
(4, 'approved',   '已通过',   'approved',   3),
(4, 'rejected',   '已驳回',   'rejected',   4),
(4, 'scheduled',  '已排期',   'scheduled',  5),
(4, 'in_progress','进行中',   'in_progress',6),
(4, 'done',       '已完成',   'done',       7);

-- 需求来源
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(5, 'business',    '业务需求',  'business',    1),
(5, 'tech',        '技术需求',  'tech',        2),
(5, 'regulatory',  '合规需求',  'regulatory',  3);

-- 需求分类
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(6, 'general',      '通用需求',    'general',      1),
(6, 'differential', '差异化需求',   'differential', 2);

-- 任务类型
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(7, 'dev',      '开发',    'dev',      1),
(7, 'test',     '测试',    'test',     2),
(7, 'deploy',   '部署',    'deploy',   3),
(7, 'training', '培训',    'training', 4),
(7, 'design',   '设计',    'design',   5),
(7, 'review',   '评审',    'review',   6),
(7, 'other',    '其他',    'other',    7);

-- 任务状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(8, 'todo',        '待办',    'todo',        1),
(8, 'in_progress', '进行中',   'in_progress', 2),
(8, 'done',        '已完成',   'done',        3),
(8, 'cancelled',   '已取消',   'cancelled',   4);

-- 里程碑状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(9, 'pending',    '待开始',   'pending',    1),
(9, 'in_progress','进行中',   'in_progress',2),
(9, 'at_risk',    '有风险',   'at_risk',    3),
(9, 'completed',  '已完成',   'completed',  4),
(9, 'delayed',    '已延期',   'delayed',    5);

-- 交付物状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(10, 'draft',     '草稿',    'draft',     1),
(10, 'submitted', '已提交',   'submitted', 2),
(10, 'approved',  '已审核',   'approved',  3),
(10, 'rejected',  '已驳回',   'rejected',  4);

-- 推广单元状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(11, 'pending',     '待启动',   'pending',     1),
(11, 'in_progress', '进行中',   'in_progress', 2),
(11, 'completed',   '已完成',   'completed',   3),
(11, 'suspended',   '已暂停',   'suspended',   4);

-- 推广进度状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(12, 'pending',     '待开始',   'pending',     1),
(12, 'in_progress', '进行中',   'in_progress', 2),
(12, 'completed',   '已完成',   'completed',   3),
(12, 'skipped',     '已跳过',   'skipped',     4);

-- 用户状态
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(13, 'active',   '正常',   'active',   1),
(13, 'disabled', '停用',   'disabled', 2),
(13, 'locked',   '锁定',   'locked',   3);

-- 组织类型
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(14, 'company',    '公司',    'company',    1),
(14, 'department', '部门',    'department', 2),
(14, 'team',       '团队',    'team',       3);

-- 依赖类型
INSERT INTO `sys_dict_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
(15, 'FS', '完成-开始', 'FS', 1),
(15, 'FF', '完成-完成', 'FF', 2),
(15, 'SS', '开始-开始', 'SS', 3),
(15, 'SF', '开始-完成', 'SF', 4);

-- ============================================================
-- 第六部分：默认推广阶段模板（全局模板）
-- ============================================================

INSERT INTO `promotion_stage_template` (`id`, `project_id`, `name`, `description`, `sort_order`, `is_locked`, `estimated_days`) VALUES
(1, NULL, '准备阶段',   '环境准备、人员培训、方案确认', 1, 1, 14),
(2, NULL, '实施阶段',   '系统部署、数据迁移、功能适配', 2, 1, 30),
(3, NULL, '试运行',    '用户试用、问题收集、性能验证', 3, 1, 21),
(4, NULL, '正式上线',   '正式切换、并行运行、监控保障', 4, 1, 14),
(5, NULL, '验收阶段',   '用户验收、文档移交、项目总结', 5, 1, 14);

-- ============================================================
-- 初始化完成
-- ============================================================

-- 验证数据
SELECT '角色数量' AS item, COUNT(*) AS count FROM sys_role WHERE is_deleted = 0
UNION ALL
SELECT '权限数量', COUNT(*) FROM sys_permission
UNION ALL
SELECT '角色-权限映射数量', COUNT(*) FROM sys_role_permission
UNION ALL
SELECT '用户数量', COUNT(*) FROM sys_user WHERE is_deleted = 0
UNION ALL
SELECT '字典数量', COUNT(*) FROM sys_dict WHERE is_deleted = 0
UNION ALL
SELECT '字典项数量', COUNT(*) FROM sys_dict_item
UNION ALL
SELECT '全局推广阶段模板', COUNT(*) FROM promotion_stage_template WHERE project_id IS NULL;
