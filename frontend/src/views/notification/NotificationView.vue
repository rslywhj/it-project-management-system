<template>
  <div class="notification-page">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-radio-group v-model="filterRead" @change="handleSearch">
          <el-radio-button :value="undefined">全部</el-radio-button>
          <el-radio-button :value="0">未读</el-radio-button>
          <el-radio-button :value="1">已读</el-radio-button>
        </el-radio-group>
      </div>
      <el-button size="small" @click="handleMarkAllRead">全部已读</el-button>
    </div>

    <el-table v-loading="loading" :data="notificationList" stripe>
      <el-table-column prop="title" label="标题" min-width="220">
        <template #default="{ row }">
          <span :class="{ 'unread': !row.isRead }">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="typeTag((row as Notification).type)">{{ typeLabel((row as Notification).type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isRead" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="(row as Notification).isRead ? 'info' : 'warning'" size="small">
            {{ (row as Notification).isRead ? '已读' : '未读' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="时间" width="170" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="!(row as Notification).isRead" link type="primary" @click="handleMarkRead(row as Notification)">标记已读</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNotificationList, markAsRead, markAllAsRead, type Notification } from '@/api/notification'

const loading = ref(false)
const notificationList = ref<Notification[]>([])
const total = ref(0)
const filterRead = ref<number | undefined>(undefined)
const queryParams = reactive({ page: 1, size: 10 })

const typeMap: Record<string, string> = {
  task: '任务', requirement: '需求', project: '项目', system: '系统',
}
const typeTagMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
  task: 'primary', requirement: 'success', project: 'warning', system: 'info',
}

function typeLabel(t: string) { return typeMap[t] ?? t }
function typeTag(t: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' { return typeTagMap[t] ?? 'info' }

async function loadData() {
  loading.value = true
  try {
    const params = { ...queryParams, isRead: filterRead.value } as Parameters<typeof getNotificationList>[0]
    const data = await getNotificationList(params)
    notificationList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

async function handleMarkRead(row: Notification) {
  try {
    await markAsRead(row.id)
    row.isRead = 1
    ElMessage.success('已标记为已读')
  } catch { /* handled */ }
}

async function handleMarkAllRead() {
  try {
    await markAllAsRead()
    ElMessage.success('全部已标记为已读')
    loadData()
  } catch { /* handled */ }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.unread {
  font-weight: 600;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
