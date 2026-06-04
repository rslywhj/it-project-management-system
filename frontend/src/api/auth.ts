import service from './index'
import type { LoginParams, LoginResult } from '@/types/api'
import type { UserInfo } from '@/stores/user'

/** 登录 */
export async function login(params: LoginParams): Promise<LoginResult> {
  return service.post('/auth/login', params)
}

/** 登出 */
export async function logout(): Promise<void> {
  return service.post('/auth/logout')
}

/** 刷新 Token（query 参数） */
export async function refreshToken(token: string): Promise<LoginResult> {
  return service.post('/auth/refresh', null, { params: { refreshToken: token } })
}

/** 获取当前用户信息 */
export async function getCurrentUser(): Promise<UserInfo> {
  return service.get('/auth/me')
}
