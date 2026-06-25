<template>
  <div class="workload-view">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>团队工时统计</span>
          <el-button size="small" @click="loadData"><el-icon><Refresh /></el-icon>刷新</el-button>
        </div>
      </template>

      <div v-loading="loading">
        <el-row :gutter="16" style="margin-bottom: 16px">
          <el-col :span="12">
            <div ref="taskDistChartRef" style="height: 300px"></div>
          </el-col>
          <el-col :span="12">
            <div ref="workloadChartRef" style="height: 300px"></div>
          </el-col>
        </el-row>

        <el-table :data="workloadList" stripe>
          <el-table-column prop="userName" label="成员" width="120" />
          <el-table-column prop="totalTasks" label="总任务" width="80" align="center" />
          <el-table-column prop="completedTasks" label="已完成" width="80" align="center">
            <template #default="{ row }">
              <span style="color: #67c23a">{{ row.completedTasks }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="inProgressTasks" label="进行中" width="80" align="center">
            <template #default="{ row }">
              <span style="color: #e6a23c">{{ row.inProgressTasks }}</span>
            </template>
          </el-table-column>
          <el-table-column label="完成率" width="160">
            <template #default="{ row }">
              <el-progress
                :percentage="row.totalTasks > 0 ? Math.round(row.completedTasks / row.totalTasks * 100) : 0"
                :stroke-width="10"
              />
            </template>
          </el-table-column>
          <el-table-column prop="estimatedHours" label="预估工时" width="100" align="center" />
          <el-table-column prop="actualHours" label="实际工时" width="100" align="center" />
          <el-table-column label="工时偏差" width="100" align="center">
            <template #default="{ row }">
              <span :style="{ color: row.actualHours > row.estimatedHours ? '#f56c6c' : '#67c23a' }">
                {{ row.estimatedHours > 0 ? (row.actualHours - row.estimatedHours > 0 ? '+' : '') + (row.actualHours - row.estimatedHours) : '-' }}
              </span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getWorkloadData } from '@/api/report'
import type { WorkloadData } from '@/types/report'

const props = defineProps<{ projectId?: number }>()

const loading = ref(false)
const workloadList = ref<WorkloadData[]>([])
const taskDistChartRef = ref<HTMLElement | null>(null)
const workloadChartRef = ref<HTMLElement | null>(null)
let taskDistChart: echarts.ECharts | null = null
let workloadChart: echarts.ECharts | null = null

function initCharts() {
  if (taskDistChartRef.value && workloadList.value.length > 0) {
    taskDistChart = echarts.init(taskDistChartRef.value)
    taskDistChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['已完成', '进行中', '待办'] },
      xAxis: { type: 'category', data: workloadList.value.map((w) => w.userName) },
      yAxis: { type: 'value' },
      series: [
        { name: '已完成', type: 'bar', stack: 'total', data: workloadList.value.map((w) => w.completedTasks), itemStyle: { color: '#67c23a' } },
        { name: '进行中', type: 'bar', stack: 'total', data: workloadList.value.map((w) => w.inProgressTasks), itemStyle: { color: '#e6a23c' } },
        { name: '待办', type: 'bar', stack: 'total', data: workloadList.value.map((w) => w.totalTasks - w.completedTasks - w.inProgressTasks), itemStyle: { color: '#909399' } },
      ],
    })
  }

  if (workloadChartRef.value && workloadList.value.length > 0) {
    workloadChart = echarts.init(workloadChartRef.value)
    workloadChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['预估工时', '实际工时'] },
      xAxis: { type: 'category', data: workloadList.value.map((w) => w.userName) },
      yAxis: { type: 'value', name: '小时' },
      series: [
        { name: '预估工时', type: 'bar', data: workloadList.value.map((w) => w.estimatedHours), itemStyle: { color: '#409eff' } },
        { name: '实际工时', type: 'bar', data: workloadList.value.map((w) => w.actualHours), itemStyle: { color: '#e6a23c' } },
      ],
    })
  }
}

async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getWorkloadData(props.projectId)
    workloadList.value = Array.isArray(data) ? data : []
    await nextTick()
    initCharts()
  } catch { /* handled */ } finally { loading.value = false }
}

function handleResize() {
  taskDistChart?.resize()
  workloadChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  taskDistChart?.dispose()
  workloadChart?.dispose()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
