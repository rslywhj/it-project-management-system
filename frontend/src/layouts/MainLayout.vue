<template>
  <el-container class="main-layout">
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="aside">
      <Sidebar />
    </el-aside>
    <el-container>
      <el-header class="header">
        <Navbar />
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { useAppStore } from '@/stores/app'
import Sidebar from './components/Sidebar.vue'
import Navbar from './components/Navbar.vue'

const appStore = useAppStore()
</script>

<style scoped lang="scss">
.main-layout {
  height: 100vh;
  overflow: hidden;
}

.aside {
  background-color: #304156;
  transition: width 0.28s;
  overflow: hidden;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 56px;
}

.main {
  background-color: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}
</style>
