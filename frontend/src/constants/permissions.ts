/**
 * RBAC 权限码常量
 * 格式：模块:操作
 * 来源：DBA 角色权限清单（10 个角色、46 个权限码）
 */

// ========== 项目管理 ==========
export const PROJECT_VIEW = 'project:view'
export const PROJECT_CREATE = 'project:create'
export const PROJECT_EDIT = 'project:edit'
export const PROJECT_DELETE = 'project:delete'
export const PROJECT_MANAGE = 'project:manage'

// ========== 团队管理 ==========
export const TEAM_VIEW = 'team:view'
export const TEAM_CREATE = 'team:create'
export const TEAM_EDIT = 'team:edit'
export const TEAM_DELETE = 'team:delete'

// ========== 需求管理 ==========
export const REQUIREMENT_VIEW = 'requirement:view'
export const REQUIREMENT_CREATE = 'requirement:create'
export const REQUIREMENT_EDIT = 'requirement:edit'
export const REQUIREMENT_DELETE = 'requirement:delete'
export const REQUIREMENT_MANAGE = 'requirement:manage'

// ========== 任务管理 ==========
export const TASK_VIEW = 'task:view'
export const TASK_CREATE = 'task:create'
export const TASK_EDIT = 'task:edit'
export const TASK_DELETE = 'task:delete'
export const TASK_MANAGE = 'task:manage'

// ========== 里程碑管理 ==========
export const MILESTONE_VIEW = 'milestone:view'
export const MILESTONE_CREATE = 'milestone:create'
export const MILESTONE_EDIT = 'milestone:edit'
export const MILESTONE_DELETE = 'milestone:delete'

// ========== 交付物管理 ==========
export const DELIVERY_VIEW = 'delivery:view'
export const DELIVERY_CREATE = 'delivery:create'
export const DELIVERY_EDIT = 'delivery:edit'
export const DELIVERY_DELETE = 'delivery:delete'
export const DELIVERY_MANAGE = 'delivery:manage'

// ========== 测试管理 ==========
export const TEST_VIEW = 'test:view'
export const TEST_CREATE = 'test:create'
export const TEST_EDIT = 'test:edit'
export const TEST_DELETE = 'test:delete'
export const TEST_MANAGE = 'test:manage'

// ========== 推广管理 ==========
export const PROMOTION_VIEW = 'promotion:view'
export const PROMOTION_CREATE = 'promotion:create'
export const PROMOTION_EDIT = 'promotion:edit'
export const PROMOTION_DELETE = 'promotion:delete'
export const PROMOTION_MANAGE = 'promotion:manage'

// ========== 报表 ==========
export const REPORT_VIEW = 'report:view'
export const REPORT_MANAGE = 'report:manage'

// ========== 系统管理 ==========
export const SYSTEM_USER = 'system:user'
export const SYSTEM_ROLE = 'system:role'
export const SYSTEM_PERMISSION = 'system:permission'
export const SYSTEM_DICT = 'system:dict'
export const SYSTEM_ORG = 'system:org'
export const SYSTEM_LOG = 'system:log'

/** 所有权限码列表 */
export const ALL_PERMISSIONS = [
  PROJECT_VIEW, PROJECT_CREATE, PROJECT_EDIT, PROJECT_DELETE, PROJECT_MANAGE,
  TEAM_VIEW, TEAM_CREATE, TEAM_EDIT, TEAM_DELETE,
  REQUIREMENT_VIEW, REQUIREMENT_CREATE, REQUIREMENT_EDIT, REQUIREMENT_DELETE, REQUIREMENT_MANAGE,
  TASK_VIEW, TASK_CREATE, TASK_EDIT, TASK_DELETE, TASK_MANAGE,
  MILESTONE_VIEW, MILESTONE_CREATE, MILESTONE_EDIT, MILESTONE_DELETE,
  DELIVERY_VIEW, DELIVERY_CREATE, DELIVERY_EDIT, DELIVERY_DELETE, DELIVERY_MANAGE,
  TEST_VIEW, TEST_CREATE, TEST_EDIT, TEST_DELETE, TEST_MANAGE,
  PROMOTION_VIEW, PROMOTION_CREATE, PROMOTION_EDIT, PROMOTION_DELETE, PROMOTION_MANAGE,
  REPORT_VIEW, REPORT_MANAGE,
  SYSTEM_USER, SYSTEM_ROLE, SYSTEM_PERMISSION, SYSTEM_DICT, SYSTEM_ORG, SYSTEM_LOG,
] as const

export type Permission = (typeof ALL_PERMISSIONS)[number]

/** 角色编码枚举 */
export const ROLE_SUPER_ADMIN = 'super_admin'
export const ROLE_PROJECT_ADMIN = 'project_admin'
export const ROLE_PROJECT_MANAGER = 'project_manager'
export const ROLE_PROMOTION_MANAGER = 'promotion_manager'
export const ROLE_PROMOTION_UNIT_LEAD = 'promotion_unit_lead'
export const ROLE_PRODUCT_MANAGER = 'product_manager'
export const ROLE_DEVELOPER = 'developer'
export const ROLE_TESTER = 'tester'
export const ROLE_EXTERNAL_COLLABORATOR = 'external_collaborator'
export const ROLE_GUEST = 'guest'
