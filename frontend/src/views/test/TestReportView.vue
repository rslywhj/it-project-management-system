<template>
  <div class="test-report" v-loading="loading">
    <!-- 筛选条件 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="selectedPlanId" placeholder="选择测试计划" clearable style="width: 240px" @change="loadAllData">
          <el-option label="全部计划" :value="undefined" />
          <el-option v-for="plan in planOptions" :key="plan.id" :label="plan.name" :value="plan.id" />
        </el-select>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ summary.totalCases }}</div>
          <div class="stat-label">用例总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card success">
          <div class="stat-value">{{ summary.passCases }}</div>
          <div class="stat-label">通过</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card danger">
          <div class="stat-value">{{ summary.failCases }}</div>
          <div class="stat-label">失败</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card warning">
          <div class="stat-value">{{ summary.blockCases }}</div>
          <div class="stat-label">阻塞</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ summary.passRate }}%</div>
          <div class="stat-label">通过率</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card info">
          <div class="stat-value">{{ summary.totalDefects }}</div>
          <div class="stat-label">缺陷总数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>用例执行结果分布</span>
          </template>
          <div ref="caseResultChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>缺陷严重程度分布</span>
          </template>
          <div ref="defectSeverityChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>缺陷状态分布</span>
          </template>
          <div ref="defectStatusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <span>缺陷趋势</span>
          </template>
          <div ref="defectTrendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  getTestReportSummary,
  getDefectDistribution,
  getDefectTrend,
  getTestPlanList,
} from '@/api/test'
import type { TestReportSummary, DefectDistribution, DefectTrend, TestPlan } from '@/types/test'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const selectedPlanId = ref<number | undefined>(undefined)
const planOptions = ref<TestPlan[]>([])

const summary = reactive<TestReportSummary>({
  totalCases: 0,
  passCases: 0,
  failCases: 0,
  blockCases: 0,
  skipCases: 0,
  pendingCases: 0,
  passRate: 0,
  totalDefects: 0,
  openDefects: 0,
  resolvedDefects: 0,
  closedDefects: 0,
})

const severityDistribution = ref<DefectDistribution[]>([])
const statusDistribution = ref<DefectDistribution[]>([])
const defectTrendData = ref<DefectTrend[]>([])

// 图表引用
const caseResultChartRef = ref<HTMLDivElement>()
const defectSeverityChartRef = ref<HTMLDivElement>()
const defectStatusChartRef = ref<HTMLDivElement>()
const defectTrendChartRef = ref<HTMLDivElement>()

let caseResultChart: echarts.ECharts | null = null
let defectSeverityChart: echarts.ECharts | null = null
let defectStatusChart: echarts.ECharts | null = null
let defectTrendChart: echarts.ECharts | null = null

async function loadPlanOptions() {
  try {
    const data = await getTestPlanList(props.projectId, { page: 1, size: 100 })
    planOptions.value = data.records
  } catch { /* handled */ }
}

async function loadSummary() {
  try {
    const data = await getTestReportSummary(props.projectId, selectedPlanId.value)
    Object.assign(summary, data)
  } catch { /* handled */ }
}

async function loadDefectDistribution() {
  try {
    const data = await getDefectDistribution(props.projectId, selectedPlanId.value)
    severityDistribution.value = data
  } catch { /* handled */ }
}

async function loadDefectTrend() {
  try {
    const data = await getDefectTrend(props.projectId, selectedPlanId.value)
    defectTrendData.value = data
  } catch { /* handled */ }
}

function initCaseResultChart() {
  if (!caseResultChartRef.value) return
  caseResultChart = echarts.init(caseResultChartRef.value)
  caseResultChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      label: { show: true, formatter: '{b}: {c} ({d}%)' },
      data: [
        { value: summary.passCases, name: '通过', itemStyle: { color: '#67c23a' } },
        { value: summary.failCases, name: '失败', itemStyle: { color: '#f56c6c' } },
        { value: summary.blockCases, name: '阻塞', itemStyle: { color: '#e6a23c' } },
        { value: summary.skipCases, name: '跳过', itemStyle: { color: '#909399' } },
        { value: summary.pendingCases, name: '未执行', itemStyle: { color: '#c0c4cc' } },
      ],
    }],
  })
}

function initDefectSeverityChart() {
  if (!defectSeverityChartRef.value) return
  defectSeverityChart = echarts.init(defectSeverityChartRef.value)
  const colorMap: Record<string, string> = {
    '致命': '#f56c6c', '严重': '#e6a23c', '一般': '#409eff', '轻微': '#909399',
  }
  defectSeverityChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      label: { show: true, formatter: '{b}: {c} ({d}%)' },
      data: severityDistribution.value.map(item => ({
        value: item.value,
        name: item.name,
        itemStyle: { color: colorMap[item.name] || '#409eff' },
      })),
    }],
  })
}

function initDefectStatusChart() {
  if (!defectStatusChartRef.value) return
  defectStatusChart = echarts.init(defectStatusChartRef.value)
  defectStatusChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['打开', '处理中', '已解决', '已关闭', '重新打开'],
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: [
        { value: summary.openDefects, itemStyle: { color: '#409eff' } },
        { value: 0, itemStyle: { color: '#e6a23c' } },
        { value: summary.resolvedDefects, itemStyle: { color: '#67c23a' } },
        { value: summary.closedDefects, itemStyle: { color: '#909399' } },
        { value: 0, itemStyle: { color: '#f56c6c' } },
      ],
      barWidth: '40%',
    }],
  })
}

function initDefectTrendChart() {
  if (!defectTrendChartRef.value) return
  defectTrendChart = echarts.init(defectTrendChartRef.value)
  defectTrendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { bottom: 0 },
    xAxis: {
      type: 'category',
      data: defectTrendData.value.map(item => item.date),
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '新增缺陷',
        type: 'line',
        data: defectTrendData.value.map(item => item.created),
        itemStyle: { color: '#f56c6c' },
        smooth: true,
      },
      {
        name: '已解决缺陷',
        type: 'line',
        data: defectTrendData.value.map(item => item.resolved),
        itemStyle: { color: '#67c23a' },
        smooth: true,
      },
    ],
  })
}

function renderCharts() {
  nextTick(() => {
    initCaseResultChart()
    initDefectSeverityChart()
    initDefectStatusChart()
    initDefectTrendChart()
  })
}

function handleResize() {
  caseResultChart?.resize()
  defectSeverityChart?.resize()
  defectStatusChart?.resize()
  defectTrendChart?.resize()
}

async function loadAllData() {
  loading.value = true
  try {
    await Promise.all([loadSummary(), loadDefectDistribution(), loadDefectTrend()])
    renderCharts()
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadPlanOptions()
  await loadAllData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  caseResultChart?.dispose()
  defectSeverityChart?.dispose()
  defectStatusChart?.dispose()
  defectTrendChart?.dispose()
})
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .toolbar-left {
    display: flex;
    gap: 8px;
  }
}

.stat-card {
  text-align: center;

  .stat-value {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
  }

  .stat-label {
    margin-top: 4px;
    color: #909399;
    font-size: 13px;
  }

  &.success .stat-value { color: #67c23a; }
  &.warning .stat-value { color: #e6a23c; }
  &.danger .stat-value { color: #f56c6c; }
  &.info .stat-value { color: #909399; }
}

.chart-container {
  width: 100%;
  height: 300px;
}
</style>
