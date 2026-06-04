import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '@/api/auth'
import { setToken, setRefreshToken, clearTokens } from '@/utils/auth'

/** 用户信息（对齐后端 /auth/me 响应） */
export interface UserInfo {
  userId: number
  username: string
  realName: string
  role: string
  roles: string[]
  permissions: string[]
  orgId?: number
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  const isLoggedIn = computed(() => !!userInfo.value)
  const username = computed(() => userInfo.value?.username ?? '')
  const realName = computed(() => userInfo.value?.realName ?? '')

  /** 登录 — interceptor returns LoginResult directly */
  async function login(params: { username: string; password: string }) {
    const res = await loginApi(params)
    setToken(res.accessToken)
    setRefreshToken(res.refreshToken)
    // 登录接口返回基础用户信息，立即设置
    const loginUser = res.userInfo
    userInfo.value = {
      userId: loginUser.userId,
      username: loginUser.username,
      realName: loginUser.realName,
      role: loginUser.role,
      roles: [loginUser.role],
      permissions: [],
      orgId: loginUser.orgId,
    }
    roles.value = [loginUser.role]
    // 获取完整用户信息（含权限列表）
    await fetchUserInfo()
  }

  /** 获取用户信息 — interceptor returns UserInfo directly */
  async function fetchUserInfo() {
    try {
      const data = await getCurrentUser()
      userInfo.value = data
      roles.value = data.roles || [data.role]
      permissions.value = data.permissions || []
    } catch {
      // 如果 /auth/me 接口不可用，使用登录时的基础信息
      // super_admin 拥有全部权限
      if (userInfo.value?.role === 'super_admin') {
        permissions.value = ['*']
      }
    }
  }

  /** 登出 */
  async function logout() {
    try {
      await logoutApi()
    } finally {
      resetState()
    }
  }

  /** 重置状态 */
  function resetState() {
    userInfo.value = null
    roles.value = []
    permissions.value = []
    clearTokens()
  }

  /** 检查是否拥有指定角色 */
  function hasRole(role: string): boolean {
    return roles.value.includes(role)
  }

  /** 检查是否拥有任一权限 */
  function hasPermission(perms: string | string[]): boolean {
    if (permissions.value.includes('*')) return true
    const permList = Array.isArray(perms) ? perms : [perms]
    return permList.some((p) => permissions.value.includes(p))
  }

  /** 检查是否拥有全部权限 */
  function hasAllPermissions(perms: string[]): boolean {
    if (permissions.value.includes('*')) return true
    return perms.every((p) => permissions.value.includes(p))
  }

  return {
    userInfo,
    roles,
    permissions,
    isLoggedIn,
    username,
    realName,
    login,
    fetchUserInfo,
    logout,
    resetState,
    hasRole,
    hasPermission,
    hasAllPermissions,
  }
})
