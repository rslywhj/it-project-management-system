<template>
  <div class="project-dashboard" v-loading="loading">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">需求总数</span>
              <span class="value">{{ stats.requirementStats.total }}</span>
            </div>
            <el-icon :size="36" color="#409eff"><Document /></el-icon>
          </div>
          <div class="stat-footer">
            完成率 {{ stats.requirementStats.completionRate }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">任务总数</span>
              <span class="value">{{ stats.taskStats.total }}</span>
            </div>
            <el-icon :size="36" color="#67c23a"><Finished /></el-icon>
          </div>
          <div class="stat-footer">
            完成率 {{ stats.taskStats.completionRate }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">里程碑</span>
              <span class="value">{{ stats.milestoneStats.total }}</span>
            </div>
            <el-icon :size="36" color="#e6a23c"><Flag /></el-icon>
          </div>
          <div class="stat-footer">
            已完成 {{ stats.milestoneStats.completed }}，延期 {{ stats.milestoneStats.overdue.length }}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">整体进度</span>
              <span class="value">{{ stats.stats.overallProgress }}%</span>
            </div>
            <el-icon :size="36" color="#909399"><TrendCharts /></el-icon>
          </div>
          <div class="stat-footer">
            {{ stats.stats.totalMembers }} 名成员参与
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
          <template #header><span>即将到期里程碑</span></template>
          <div v-if="stats.milestoneStats.upcoming.length > 0">
            <div v-for="m in stats.milestoneStats.upcoming" :key="m.id" class="milestone-item">
              <div class="milestone-name">{{ m.name }}</div>
              <div class="milestone-date">{{ m.plannedDate }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无即将到期的里程碑" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近活动 -->
    <el-card shadow="never">
      <template #header><span>最近活动</span></template>
      <el-timeline v-if="stats.recentActivities.length > 0">
        <el-timeline-item
          v-for="activity in stats.recentActivities"
          :key="activity.id"
          :timestamp="activity.createdAt"
          placement="top"
        >
          <span class="activity-user">{{ activity.userName }}</span>
          <span class="activity-title">{{ activity.title }}</span>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无活动记录" :image-size="60" />
    </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getProjectDashboard, getBurndownData } from '@/api/report'
import type { ProjectDashboard, BurndownData } from '@/types/report'

const props = defineProps<{ projectId?: number }>()

const hasProject = computed(() => !!props.projectId && props.projectId > 0)
const loading = ref(false)
const stats = reactive<ProjectDashboard>({
  projectId: 0,
  projectName: '',
  stats: { totalRequirements: 0, totalTasks: 0, totalMilestones: 0, totalMembers: 0, overallProgress: 0 },
  requirementStats: { total: 0, byStatus: {}, completionRate: 0 },
  taskStats: { total: 0, byStatus: {}, completionRate: 0 },
  milestoneStats: { total: 0, completed: 0, upcoming: [], overdue: [] },
  recentActivities: [],
})

const reqChartRef = ref<HTMLElement | null>(null)
const taskChartRef = ref<HTMLElement | null>(null)
const burndownChartRef = ref<HTMLElement | null>(null)

let reqChart: echarts.ECharts | null = null
let taskChart: echarts.ECharts | null = null
let burndownChart: echarts.ECharts | null = null

const reqStatusMap: Record<string, string> = {
  draft: '草稿', reviewing: '评审中', approved: '已通过', rejected: '已驳回', scheduled: '已排期', done: '已完成',
}
const reqColorMap: Record<string, string> = {
  draft: '#909399', reviewing: '#e6a23c', approved: '#67c23a', rejected: '#f56c6c', scheduled: '#409eff', done: '#67c23a',
}
const taskStatusMap: Record<string, string> = { todo: '待办', in_progress: '进行中', done: '已完成' }
const taskColorMap: Record<string, string> = { todo: '#909399', in_progress: '#e6a23c', done: '#67c23a' }

function initCharts() {
  if (reqChartRef.value) {
    reqChart = echarts.init(reqChartRef.value)
    const data = Object.entries(stats.requirementStats.byStatus).map(([key, value]) => ({
      name: reqStatusMap[key] ?? key,
      value,
      itemStyle: { color: reqColorMap[key] ?? '#409eff' },
    }))
    reqChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
        data, label: { show: true, formatter: '{b}: {c}' },
      }],
    })
  }

  if (taskChartRef.value) {
    taskChart = echarts.init(taskChartRef.value)
    const data = Object.entries(stats.taskStats.byStatus).map(([key, value]) => ({
      name: taskStatusMap[key] ?? key,
      value,
      itemStyle: { color: taskColorMap[key] ?? '#409eff' },
    }))
    taskChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie', radius: ['40%', '70%'], center: ['50%', '45%'],
        data, label: { show: true, formatter: '{b}: {c}' },
      }],
    })
  }
}

async function refreshBurndown() {
  if (!props.projectId) return
  try {
    const data = await getBurndownData(props.projectId)
    renderBurndown(data)
  } catch { /* handled */ }
}

function renderBurndown(data: BurndownData) {
  if (!burndownChartRef.value) return
  if (!burndownChart) burndownChart = echarts.init(burndownChartRef.value)
  burndownChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['理想线', '实际线'] },
    xAxis: { type: 'category', data: data.dates },
    yAxis: { type: 'value', name: '剩余任务数' },
    series: [
      { name: '理想线', type: 'line', data: data.ideal, lineStyle: { type: 'dashed' }, itemStyle: { color: '#909399' } },
      { name: '实际线', type: 'line', data: data.actual, itemStyle: { color: '#409eff' }, areaStyle: { color: 'rgba(64,158,255,0.1)' } },
    ],
  })
}

async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getProjectDashboard(props.projectId)
    Object.assign(stats, data)
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

.milestone-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  &:last-child { border-bottom: none; }
  .milestone-name { font-size: 14px; color: #303133; }
  .milestone-date { font-size: 12px; color: #909399; margin-top: 2px; }
}

.activity-user { font-weight: 500; margin-right: 8px; }
.activity-title { color: #606266; }
</style>
