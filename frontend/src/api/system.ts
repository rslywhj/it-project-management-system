import service from './index'
import type { PageParams, PageResult } from '@/types/api'
import type { SysUser, UserCreateRequest, UserUpdateRequest, SysRole, RoleRequest } from '@/types/system'

// ==================== 用户管理 ====================

/** 分页查询用户列表 */
export function getUserList(params: PageParams & { keyword?: string; status?: string }) {
  return service.get<PageResult<SysUser>>('/system/users', { params })
}

/** 获取用户详情 */
export function getUserDetail(id: number) {
  return service.get<SysUser>(`/system/users/${id}`)
}

/** 创建用户 */
export function createUser(data: UserCreateRequest) {
  return service.post<SysUser>('/system/users', data)
}

/** 更新用户 */
export function updateUser(id: number, data: UserUpdateRequest) {
  return service.put<SysUser>(`/system/users/${id}`, data)
}

/** 删除用户 */
export function deleteUser(id: number) {
  return service.delete(`/system/users/${id}`)
}

/** 更新用户状态 */
export function updateUserStatus(id: number, status: string) {
  return service.put(`/system/users/${id}/status`, null, { params: { status } })
}

/** 重置用户密码 */
export function resetUserPassword(id: number, newPassword: string) {
  return service.put(`/system/users/${id}/password/reset`, null, { params: { newPassword } })
}

// ==================== 角色管理 ====================

/** 查询角色列表 */
export function getRoleList() {
  return service.get<SysRole[]>('/system/roles')
}

/** 获取角色详情 */
export function getRoleDetail(id: number) {
  return service.get<SysRole>(`/system/roles/${id}`)
}

/** 创建角色 */
export function createRole(data: RoleRequest) {
  return service.post<SysRole>('/system/roles', data)
}

/** 更新角色 */
export function updateRole(id: number, data: RoleRequest) {
  return service.put<SysRole>(`/system/roles/${id}`, data)
}

/** 删除角色 */
export function deleteRole(id: number) {
  return service.delete(`/system/roles/${id}`)
}
