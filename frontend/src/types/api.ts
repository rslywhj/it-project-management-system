/** 统一响应结构 */
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
}

/** 分页请求参数 */
export interface PageParams {
  page: number
  pageSize: number
}

/** 分页响应 */
export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

/** 登录请求 */
export interface LoginParams {
  username: string
  password: string
}

/** 登录响应 */
export interface LoginResult {
  accessToken: string
  refreshToken: string
  expiresIn: number
}
