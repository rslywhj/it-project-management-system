/** 用户信息 */
export interface UserInfo {
  id: string
  username: string
  realName: string
  avatar?: string
  email?: string
  phone?: string
  orgId?: string
  orgName?: string
  roles: string[]
  permissions: string[]
}
