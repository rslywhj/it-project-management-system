import service from './index'
import type { ApiResult, LoginParams, LoginResult } from '@/types/api'
import type { UserInfo } from '@/types/user'

/** 登录 */
export function login(params: LoginParams) {
  return service.post<ApiResult<LoginResult>>('/auth/login', params)
}

/** 登出 */
export function logout() {
  return service.post<ApiResult<void>>('/auth/logout')
}

/** 刷新 Token */
export function refreshToken(token: string) {
  return service.post<ApiResult<LoginResult>>('/auth/refresh', { refreshToken: token })
}

/** 获取当前用户信息 */
export function getCurrentUser() {
  return service.get<ApiResult<UserInfo>>('/auth/me')
}
