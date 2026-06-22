import service from './index'
import type { PageParams, PageResult } from '@/types/api'

// ─── 用户管理 ─────────────────────────────────────────────────

export interface SysUser {
  id: number
  username: string
  realName: string
  email?: string
  phone?: string
  avatar?: string
  orgId?: number
  status: string
  lastLoginAt?: string
  remark?: string
  roles?: string[]
  createdAt: string
}

export interface UserCreateRequest {
  username: string
  password: string
  realName: string
  email?: string
  phone?: string
  orgId?: number
  roleIds?: number[]
}

export interface UserUpdateRequest {
  realName?: string
  email?: string
  phone?: string
  orgId?: number
  status?: string
  remark?: string
  roleIds?: number[]
}

/** 用户列表 */
export async function getUserList(params: PageParams & { keyword?: string; status?: string }): Promise<PageResult<SysUser>> {
  return service.get('/system/users', { params })
}

/** 用户详情 */
export async function getUserDetail(id: number): Promise<SysUser> {
  return service.get(`/system/users/${id}`)
}

/** 创建用户 */
export async function createUser(data: UserCreateRequest): Promise<SysUser> {
  return service.post('/system/users', data)
}

/** 更新用户 */
export async function updateUser(id: number, data: UserUpdateRequest): Promise<void> {
  return service.put(`/system/users/${id}`, data)
}

/** 删除用户 */
export async function deleteUser(id: number): Promise<void> {
  return service.delete(`/system/users/${id}`)
}

/** 重置密码 */
export async function resetUserPassword(id: number, newPassword: string): Promise<void> {
  return service.put(`/system/users/${id}/password`, { newPassword })
}

// ─── 角色管理 ─────────────────────────────────────────────────

export interface SysRole {
  id: number
  roleCode: string
  roleName: string
  description?: string
  dataScope: string
  sortOrder: number
  status: string
  isSystem: number
  permissionIds?: number[]
  createdAt: string
}

export interface RoleCreateRequest {
  roleCode: string
  roleName: string
  description?: string
  dataScope?: string
  sortOrder?: number
  permissionIds?: number[]
}

export interface RoleUpdateRequest {
  roleName?: string
  description?: string
  dataScope?: string
  sortOrder?: number
  status?: string
  permissionIds?: number[]
}

/** 角色列表 */
export async function getRoleList(params?: PageParams & { keyword?: string }): Promise<PageResult<SysRole>> {
  return service.get('/system/roles', { params })
}

/** 角色详情 */
export async function getRoleDetail(id: number): Promise<SysRole> {
  return service.get(`/system/roles/${id}`)
}

/** 创建角色 */
export async function createRole(data: RoleCreateRequest): Promise<SysRole> {
  return service.post('/system/roles', data)
}

/** 更新角色 */
export async function updateRole(id: number, data: RoleUpdateRequest): Promise<void> {
  return service.put(`/system/roles/${id}`, data)
}

/** 删除角色 */
export async function deleteRole(id: number): Promise<void> {
  return service.delete(`/system/roles/${id}`)
}

// ─── 权限管理 ─────────────────────────────────────────────────

export interface SysPermission {
  id: number
  permissionCode: string
  permissionName: string
  parentId: number
  type: string
  sort: number
}

/** 权限树 */
export async function getPermissionTree(): Promise<SysPermission[]> {
  return service.get('/system/permissions/tree')
}

// ─── 仪表盘 ──────────────────────────────────────────────────

export interface DashboardStats {
  projectCount: number
  pendingRequirements: number
  activeTasks: number
  upcomingMilestones: number
  recentTasks: Array<{ id: number; title: string; status: string; deadline?: string }>
}

/** 全局仪表盘统计 */
export async function getDashboardStats(): Promise<DashboardStats> {
  return service.get('/dashboard/stats')
}
