import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, getCurrentUser } from '@/api/auth'
import { setToken, setRefreshToken, clearTokens } from '@/utils/auth'
import type { UserInfo } from '@/types/user'
import type { LoginParams } from '@/types/api'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const roles = ref<string[]>([])
  const permissions = ref<string[]>([])

  const isLoggedIn = computed(() => !!userInfo.value)
  const username = computed(() => userInfo.value?.username ?? '')
  const realName = computed(() => userInfo.value?.realName ?? '')

  /** 登录 */
  async function login(params: LoginParams) {
    const { data } = await loginApi(params)
    setToken(data.accessToken)
    setRefreshToken(data.refreshToken)
    await fetchUserInfo()
  }

  /** 获取用户信息 */
  async function fetchUserInfo() {
    const { data } = await getCurrentUser()
    userInfo.value = data
    roles.value = data.roles
    permissions.value = data.permissions
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
    const permList = Array.isArray(perms) ? perms : [perms]
    return permList.some((p) => permissions.value.includes(p))
  }

  /** 检查是否拥有全部权限 */
  function hasAllPermissions(perms: string[]): boolean {
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
