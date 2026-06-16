<template>
  <div class="workload-view">
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
            <div ref="workloadChartRef" style="height: 300px"></div>
          </el-col>
          <el-col :span="12">
            <div ref="hoursChartRef" style="height: 300px"></div>
          </el-col>
        </el-row>

        <el-table :data="report?.resourceWorkloads ?? []" stripe>
          <el-table-column prop="realName" label="成员" width="120">
            <template #default="{ row }">{{ row.realName || row.username }}</template>
          </el-table-column>
          <el-table-column prop="availability" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="availType(row.availability)" size="small">{{ availLabel(row.availability) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="负载" width="160">
            <template #default="{ row }">
              <el-progress :percentage="row.workloadPercent ?? 0" :stroke-width="10" :status="(row.workloadPercent ?? 0) > 80 ? 'exception' : undefined" />
            </template>
          </el-table-column>
          <el-table-column prop="totalHoursThisWeek" label="本周工时" width="100" align="center" />
          <el-table-column prop="totalHoursThisMonth" label="本月工时" width="100" align="center" />
        </el-table>

        <div v-if="report?.workHoursSummary" class="summary-bar">
          <span>本周总工时: <strong>{{ report.workHoursSummary.totalHoursThisWeek }}h</strong></span>
          <span>本月总工时: <strong>{{ report.workHoursSummary.totalHoursThisMonth }}h</strong></span>
          <span>人均工时: <strong>{{ report.workHoursSummary.averageHoursPerPerson }}h</strong></span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getWorkloadReport } from '@/api/resource'
import type { WorkloadReport } from '@/types/resource'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const report = ref<WorkloadReport | null>(null)
const workloadChartRef = ref<HTMLElement | null>(null)
const hoursChartRef = ref<HTMLElement | null>(null)
let workloadChart: echarts.ECharts | null = null
let hoursChart: echarts.ECharts | null = null

const availMap: Record<string, string> = { available: '可用', busy: '忙碌', unavailable: '不可用', on_leave: '休假' }
const availTypeMap: Record<string, 'success' | 'warning' | 'info' | 'danger'> = { available: 'success', busy: 'warning', unavailable: 'info', on_leave: 'danger' }
function availLabel(s: string) { return availMap[s] ?? s }
function availType(s: string): 'success' | 'warning' | 'info' | 'danger' { return availTypeMap[s] ?? 'info' }

function initCharts() {
  const data = report.value?.resourceWorkloads ?? []
  if (data.length === 0) return

  if (workloadChartRef.value) {
    workloadChart = echarts.init(workloadChartRef.value)
    workloadChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: data.map(r => r.realName || r.username) },
      yAxis: { type: 'value', name: '负载%', max: 100 },
      series: [{
        type: 'bar',
        data: data.map(r => ({
          value: r.workloadPercent ?? 0,
          itemStyle: { color: (r.workloadPercent ?? 0) > 80 ? '#f56c6c' : (r.workloadPercent ?? 0) > 60 ? '#e6a23c' : '#67c23a' },
        })),
      }],
    })
  }

  if (hoursChartRef.value) {
    hoursChart = echarts.init(hoursChartRef.value)
    hoursChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['本周工时', '本月工时'] },
      xAxis: { type: 'category', data: data.map(r => r.realName || r.username) },
      yAxis: { type: 'value', name: '小时' },
      series: [
        { name: '本周工时', type: 'bar', data: data.map(r => r.totalHoursThisWeek), itemStyle: { color: '#409eff' } },
        { name: '本月工时', type: 'bar', data: data.map(r => r.totalHoursThisMonth), itemStyle: { color: '#e6a23c' } },
      ],
    })
  }
}

async function loadData() {
  loading.value = true
  try {
    report.value = await getWorkloadReport(props.projectId)
    await nextTick()
    initCharts()
  } catch { /* handled */ } finally { loading.value = false }
}

function handleResize() {
  workloadChart?.resize()
  hoursChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  workloadChart?.dispose()
  hoursChart?.dispose()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.summary-bar {
  margin-top: 16px;
  display: flex;
  gap: 24px;
  color: #909399;
  font-size: 13px;
}
</style>
