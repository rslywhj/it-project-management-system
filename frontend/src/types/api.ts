/** 统一响应结构 */
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: number
}

/** 分页请求参数（对齐后端：size） */
export interface PageParams {
  page: number
  size: number
}

/** 分页响应（对齐后端：records/total/page/size） */
export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
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
  tokenType: string
  expiresIn: number
  userInfo: LoginUserInfo
}

/** 登录响应中的用户信息 */
export interface LoginUserInfo {
  userId: number
  username: string
  realName: string
  role: string
  orgId?: number
}
