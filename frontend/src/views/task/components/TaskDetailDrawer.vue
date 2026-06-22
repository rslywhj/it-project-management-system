<template>
  <el-drawer v-model="visible" title="任务详情" size="520px" destroy-on-close>
    <div v-loading="loading" class="task-detail">
      <template v-if="task">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="WBS">{{ task.wbsCode ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="标题">{{ task.title }}</el-descriptions-item>
          <el-descriptions-item label="类型">
            <el-tag size="small" type="info">{{ typeLabel(task.type) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(task.status)" size="small">{{ statusLabel(task.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="priorityType(task.priority)" size="small">{{ task.priority }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="进度">
            <el-progress :percentage="Number(task.completionRate)" :stroke-width="8" style="width: 200px" />
          </el-descriptions-item>
          <el-descriptions-item label="计划开始">{{ task.plannedStart ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="计划结束">{{ task.plannedEnd ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际开始">{{ task.actualStart ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际结束">{{ task.actualEnd ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ task.description ?? '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 子任务 -->
        <el-divider>子任务</el-divider>
        <el-table v-if="subtasks.length" :data="subtasks" size="small" stripe>
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="completionRate" label="进度" width="100">
            <template #default="{ row }">
              <el-progress :percentage="Number(row.completionRate)" :stroke-width="4" />
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无子任务" :image-size="60" />
      </template>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getTaskDetail, getSubtasks } from '@/api/task'
import type { Task } from '@/types/task'

const visible = defineModel<boolean>({ default: false })
const props = defineProps<{ taskId: number | null }>()

const loading = ref(false)
const task = ref<Task | null>(null)
const subtasks = ref<Task[]>([])

const typeMap: Record<string, string> = { dev: '开发', test: '测试', design: '设计', research: '调研', deploy: '部署', other: '其他' }
const statusLabelMap: Record<string, string> = { todo: '待办', in_progress: '进行中', done: '已完成' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success'> = { todo: 'info', in_progress: 'warning', done: 'success' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }

function typeLabel(t: string) { return typeMap[t] ?? t }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string) { return statusTypeMap[s] ?? 'info' }
function priorityType(p: string) { return priorityTypeMap[p] ?? 'info' }

watch(() => props.taskId, async (id) => {
  if (!id || !visible.value) return
  loading.value = true
  try {
    task.value = await getTaskDetail(id)
    subtasks.value = await getSubtasks(id).catch(() => [])
  } catch { /* handled */ } finally {
    loading.value = false
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
.task-detail {
  padding: 0 4px;
}
</style>
