<template>
  <div class="sidebar">
    <div class="logo">
      <el-icon :size="24" color="#409eff"><Monitor /></el-icon>
      <span v-show="!appStore.sidebarCollapsed" class="title">项目管理系统</span>
    </div>
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        :collapse-transition="false"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 单个子路由：直接渲染菜单项 -->
          <el-menu-item
            v-if="route.children && route.children.length === 1 && !route.children[0].children"
            :key="route.path"
            :index="getMenuPath(route, route.children[0])"
          >
            <el-icon v-if="route.children[0].meta?.icon">
              <component :is="route.children[0].meta.icon" />
            </el-icon>
            <template #title>{{ route.children[0].meta?.title }}</template>
          </el-menu-item>

          <!-- 多个子路由：渲染子菜单 -->
          <el-sub-menu v-else-if="route.children && route.children.length > 0" :index="route.path">
            <template #title>
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="getMenuPath(route, child)"
            >
              <el-icon v-if="child.meta?.icon">
                <component :is="child.meta.icon" />
              </el-icon>
              <template #title>{{ child.meta?.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

/** 过滤有权限的路由 */
const menuRoutes = computed(() => {
  return router.options.routes
    .filter((r) => !r.meta?.hidden)
    .filter((r) => {
      // 公开路由或错误路由不显示在菜单
      if (r.path === '/login' || r.path === '/403' || r.path.includes(':pathMatch')) {
        return false
      }
      return true
    })
    .map((r) => {
      // 过滤子路由中无权限的
      if (r.children) {
        return {
          ...r,
          children: r.children.filter((child) => {
            const perms = child.meta?.permissions as string[] | undefined
            if (!perms) return true
            return userStore.hasPermission(perms)
          }),
        }
      }
      return r
    })
    .filter((r) => r.children && r.children.length > 0)
})

function getMenuPath(parent: { path: string }, child: { path: string }): string {
  const parentPath = parent.path.endsWith('/') ? parent.path : parent.path + '/'
  return child.path.startsWith('/') ? child.path : parentPath + child.path
}
</script>

<style scoped lang="scss">
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background-color: #2b2f3a;

  .title {
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    white-space: nowrap;
  }
}

.el-menu {
  border-right: none;
}
</style>
