import service from './index'

export interface ProfileUpdateRequest {
  realName?: string
  email?: string
  phone?: string
  avatar?: string
}

export interface PasswordChangeRequest {
  oldPassword: string
  newPassword: string
}

/** 获取个人资料 */
export async function getProfile(): Promise<Record<string, unknown>> {
  return service.get('/auth/me')
}

/** 更新个人资料 */
export async function updateProfile(data: ProfileUpdateRequest): Promise<void> {
  return service.put('/auth/profile', data)
}

/** 修改密码 */
export async function changePassword(data: PasswordChangeRequest): Promise<void> {
  return service.put('/auth/password', data)
}
