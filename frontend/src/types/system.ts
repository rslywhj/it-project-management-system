/**
 * 系统管理相关类型定义
 */

/** 用户信息 */
export interface SysUser {
  userId: number
  username: string
  realName?: string
  email?: string
  phone?: string
  avatar?: string
  orgId?: number
  orgName?: string
  status: 'active' | 'disabled' | 'locked'
  remark?: string
  roles: SysRole[]
  lastLoginAt?: string
  createdAt: string
}

/** 用户创建请求 */
export interface UserCreateRequest {
  username: string
  password: string
  realName?: string
  email?: string
  phone?: string
  orgId?: number
  remark?: string
  roleIds?: number[]
}

/** 用户更新请求 */
export interface UserUpdateRequest {
  realName?: string
  email?: string
  phone?: string
  avatar?: string
  orgId?: number
  remark?: string
  roleIds?: number[]
}

/** 角色信息 */
export interface SysRole {
  id: number
  roleCode: string
  roleName: string
  description?: string
  dataScope: 'all' | 'project' | 'promotion_unit' | 'self'
  sortOrder: number
  status: 'active' | 'disabled'
  isSystem: number
  permissionIds?: number[]
  createdAt: string
}

/** 角色创建/更新请求 */
export interface RoleRequest {
  roleCode: string
  roleName: string
  description?: string
  dataScope?: string
  sortOrder?: number
  permissionIds?: number[]
}

/** 权限信息 */
export interface SysPermission {
  id: number
  permissionCode: string
  permissionName: string
  module: string
  operation: string
  description?: string
  sortOrder: number
}
