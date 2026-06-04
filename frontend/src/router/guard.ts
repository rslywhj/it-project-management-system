import type { Router } from 'vue-router'
import NProgress from 'nprogress'
import { useUserStore } from '@/stores/user'
import { isAuthenticated } from '@/utils/auth'

NProgress.configure({ showSpinner: false })

/** 白名单路由：无需登录即可访问 */
const WHITE_LIST = ['/login']

export function setupRouterGuards(router: Router) {
  router.beforeEach(async (to, _from, next) => {
    NProgress.start()

    // 1. 已登录
    if (isAuthenticated()) {
      if (to.path === '/login') {
        // 已登录访问登录页，重定向到首页
        next({ path: '/' })
        return
      }

      const userStore = useUserStore()

      // 若用户信息未加载，先加载
      if (!userStore.isLoggedIn) {
        try {
          await userStore.fetchUserInfo()
        } catch {
          // Token 失效，清除并跳转登录
          userStore.resetState()
          next({ path: '/login', query: { redirect: to.fullPath } })
          return
        }
      }

      // 2. 权限校验：检查路由 meta.permissions
      const requiredPermissions = to.meta.permissions as string[] | undefined
      if (requiredPermissions && requiredPermissions.length > 0) {
        if (!userStore.hasPermission(requiredPermissions)) {
          next({ path: '/403' })
          return
        }
      }

      next()
      return
    }

    // 3. 未登录
    if (WHITE_LIST.includes(to.path)) {
      next()
    } else {
      next({ path: '/login', query: { redirect: to.fullPath } })
    }
  })

  router.afterEach(() => {
    NProgress.done()
  })
}
