<template>
  <div class="project-dashboard" v-loading="loading">
    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">需求总数</span>
              <span class="value">{{ dashboard.requirementStats?.total ?? 0 }}</span>
            </div>
            <el-icon :size="36" color="#409eff"><Document /></el-icon>
          </div>
          <div class="stat-footer">
            完成率 {{ dashboard.requirementStats?.completionRate ?? 0 }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">任务总数</span>
              <span class="value">{{ dashboard.taskStats?.total ?? 0 }}</span>
            </div>
            <el-icon :size="36" color="#67c23a"><Finished /></el-icon>
          </div>
          <div class="stat-footer">
            完成率 {{ dashboard.taskStats?.completionRate ?? 0 }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">缺陷总数</span>
              <span class="value">{{ dashboard.bugStats?.total ?? 0 }}</span>
            </div>
            <el-icon :size="36" color="#f56c6c"><Warning /></el-icon>
          </div>
          <div class="stat-footer">
            未解决 {{ dashboard.bugStats?.open ?? 0 }}，严重 {{ dashboard.bugStats?.criticalCount ?? 0 }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">健康度</span>
              <span class="value">{{ dashboard.healthScore ?? 0 }}</span>
            </div>
            <el-icon :size="36" :color="healthColor"><TrendCharts /></el-icon>
          </div>
          <div class="stat-footer">
            里程碑 {{ dashboard.milestoneStats?.total ?? 0 }}，延期 {{ dashboard.milestoneStats?.delayed ?? 0 }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>需求状态分布</span></template>
          <div ref="reqChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header><span>任务状态分布</span></template>
          <div ref="taskChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="16">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>燃尽图</span>
              <el-button size="small" @click="refreshBurndown">刷新</el-button>
            </div>
          </template>
          <div ref="burndownChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <template #header><span>里程碑概览</span></template>
          <div class="milestone-summary">
            <div class="milestone-row"><span>待开始</span><span class="count">{{ dashboard.milestoneStats?.pending ?? 0 }}</span></div>
            <div class="milestone-row"><span>已完成</span><span class="count success">{{ dashboard.milestoneStats?.completed ?? 0 }}</span></div>
            <div class="milestone-row"><span>延期</span><span class="count danger">{{ dashboard.milestoneStats?.delayed ?? 0 }}</span></div>
            <div class="milestone-row"><span>有风险</span><span class="count warning">{{ dashboard.milestoneStats?.atRisk ?? 0 }}</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card shadow="never">
      <template #header><span>最近活动</span></template>
      <el-timeline v-if="dashboard.recentActivities?.length">
        <el-timeline-item
          v-for="(activity, idx) in dashboard.recentActivities"
          :key="idx"
          :timestamp="activity.time"
          placement="top"
        >
          <span class="activity-user">{{ activity.operator }}</span>
          <span class="activity-title">{{ activity.title }}</span>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无活动记录" :image-size="60" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getProjectDashboard, getBurndownData } from '@/api/report'
import type { ProjectDashboard, BurndownData } from '@/types/report'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const dashboard = reactive<ProjectDashboard>({
  projectId: 0,
  projectName: '',
  projectStatus: '',
  requirementStats: { total: 0, draft: 0, reviewing: 0, approved: 0, scheduled: 0, done: 0, completionRate: 0 },
  taskStats: { total: 0, todo: 0, inProgress: 0, done: 0, completionRate: 0, averageProgress: 0 },
  bugStats: { total: 0, open: 0, inProgress: 0, resolved: 0, closed: 0, criticalCount: 0, majorCount: 0 },
  milestoneStats: { total: 0, pending: 0, completed: 0, delayed: 0, atRisk: 0 },
  healthScore: 0,
  recentActivities: [],
})

const healthColor = computed(() => {
  const s = dashboard.healthScore ?? 0
  if (s >= 80) return '#67c23a'
  if (s >= 60) return '#e6a23c'
  return '#f56c6c'
})

const reqChartRef = ref<HTMLElement | null>(null)
const taskChartRef = ref<HTMLElement | null>(null)
const burndownChartRef = ref<HTMLElement | null>(null)

let reqChart: echarts.ECharts | null = null
let taskChart: echarts.ECharts | null = null
let burndownChart: echarts.ECharts | null = null

const reqStatusMap: Record<string, string> = {
  draft: '草稿', reviewing: '评审中', approved: '已通过', scheduled: '已排期', done: '已完成',
}
const reqColorMap: Record<string, string> = {
  draft: '#909399', reviewing: '#e6a23c', approved: '#67c23a', scheduled: '#409eff', done: '#67c23a',
}
const taskStatusMap: Record<string, string> = { todo: '待办', inProgress: '进行中', done: '已完成' }
const taskColorMap: Record<string, string> = { todo: '#909399', inProgress: '#e6a23c', done: '#67c23a' }

function initCharts() {
  if (reqChartRef.value && dashboard.requirementStats) {
    reqChart = echarts.init(reqChartRef.value)
    const rs = dashboard.requirementStats
    const data = [
      { name: '草稿', value: rs.draft, itemStyle: { color: '#909399' } },
      { name: '评审中', value: rs.reviewing, itemStyle: { color: '#e6a23c' } },
      { name: '已通过', value: rs.approved, itemStyle: { color: '#67c23a' } },
      { name: '已排期', value: rs.scheduled, itemStyle: { color: '#409eff' } },
      { name: '已完成', value: rs.done, itemStyle: { color: '#67c23a' } },
    ].filter(d => d.value > 0)
    reqChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'], data, label: { show: true, formatter: '{b}: {c}' } }],
    })
  }

  if (taskChartRef.value && dashboard.taskStats) {
    taskChart = echarts.init(taskChartRef.value)
    const ts = dashboard.taskStats
    const data = [
      { name: '待办', value: ts.todo, itemStyle: { color: '#909399' } },
      { name: '进行中', value: ts.inProgress, itemStyle: { color: '#e6a23c' } },
      { name: '已完成', value: ts.done, itemStyle: { color: '#67c23a' } },
    ].filter(d => d.value > 0)
    taskChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{ type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'], data, label: { show: true, formatter: '{b}: {c}' } }],
    })
  }
}

async function refreshBurndown() {
  try {
    const data = await getBurndownData(props.projectId)
    renderBurndown(data)
  } catch { /* handled */ }
}

function renderBurndown(data: BurndownData) {
  if (!burndownChartRef.value) return
  if (!burndownChart) burndownChart = echarts.init(burndownChartRef.value)
  const dates = (data.idealLine ?? []).map(p => p.date)
  const ideal = (data.idealLine ?? []).map(p => p.remaining)
  const actual = (data.actualLine ?? []).map(p => p.remaining)
  burndownChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['理想线', '实际线'] },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: '剩余任务数' },
    series: [
      { name: '理想线', type: 'line', data: ideal, lineStyle: { type: 'dashed' }, itemStyle: { color: '#909399' } },
      { name: '实际线', type: 'line', data: actual, itemStyle: { color: '#409eff' }, areaStyle: { color: 'rgba(64,158,255,0.1)' } },
    ],
  })
}

async function loadData() {
  loading.value = true
  try {
    const data = await getProjectDashboard(props.projectId)
    Object.assign(dashboard, data)
    await nextTick()
    initCharts()
    refreshBurndown()
  } catch { /* handled */ } finally { loading.value = false }
}

function handleResize() {
  reqChart?.resize()
  taskChart?.resize()
  burndownChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  reqChart?.dispose()
  taskChart?.dispose()
  burndownChart?.dispose()
})
</script>

<style scoped lang="scss">
.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .stat-info {
    display: flex;
    flex-direction: column;
    .label { font-size: 14px; color: #909399; }
    .value { font-size: 28px; font-weight: 600; color: #303133; margin-top: 4px; }
  }
  .stat-footer {
    margin-top: 8px;
    padding-top: 8px;
    border-top: 1px solid #ebeef5;
    color: #909399;
    font-size: 13px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.milestone-summary {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.milestone-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
  .count { font-weight: 600; }
  .count.success { color: #67c23a; }
  .count.danger { color: #f56c6c; }
  .count.warning { color: #e6a23c; }
}

.activity-user { font-weight: 500; margin-right: 8px; }
.activity-title { color: #606266; }
</style>
