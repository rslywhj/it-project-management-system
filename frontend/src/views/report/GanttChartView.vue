<template>
  <div class="gantt-view">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目甘特图</span>
          <div>
            <el-radio-group v-model="ganttLib" size="small">
              <el-radio-button value="vue-ganttastic">vue-ganttastic</el-radio-button>
              <el-radio-button value="dhtmlx-gantt">dhtmlx-gantt</el-radio-button>
            </el-radio-group>
            <el-button size="small" style="margin-left: 8px" @click="loadData">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
          </div>
        </div>
      </template>

      <div v-loading="loading">
        <!-- vue-ganttastic -->
        <div v-if="ganttLib === 'vue-ganttastic' && ganttBars.length > 0" class="gantt-container">
          <g-gantt-chart
            :chart-start="chartStart"
            :chart-end="chartEnd"
            precision="day"
            bar-start="start"
            bar-end="end"
            date-format="YYYY-MM-DD"
            :row-height="40"
            :grid-width="40"
          >
            <g-gantt-row
              v-for="bar in ganttBars"
              :key="bar.id"
              :label="bar.title"
              :bars="[bar]"
              :highlight-on-hover="true"
            />
          </g-gantt-chart>
        </div>

        <!-- dhtmlx-gantt -->
        <div v-if="ganttLib === 'dhtmlx-gantt'" class="gantt-container">
          <div ref="dhtmlxContainer" class="dhtmlx-wrapper"></div>
        </div>

        <el-empty v-if="!loading && ganttBars.length === 0" description="暂无任务数据" />
      </div>
    </el-card>

    <!-- 甘特图 POC 结论 -->
    <el-card shadow="never" style="margin-top: 16px">
      <template #header><span>甘特图组件选型结论</span></template>
      <el-table :data="comparisonData" stripe size="small">
        <el-table-column prop="dimension" label="对比维度" width="150" />
        <el-table-column prop="vueGanttastic" label="vue-ganttastic" />
        <el-table-column prop="dhtmlxGantt" label="dhtmlx-gantt" />
      </el-table>
      <div style="margin-top: 16px; padding: 12px; background: #f0f9ff; border-radius: 4px; border-left: 4px solid #409eff">
        <strong>推荐方案：</strong>
        <p style="margin: 8px 0 0; color: #606266">
          <strong>短期（第一期 MVP）</strong>：使用 <code>vue-ganttastic</code>，轻量、Vue 原生、无需许可证，满足基础甘特图需求。<br>
          <strong>长期（功能完善期）</strong>：如需拖拽、关键路径、资源视图等高级特性，评估 <code>dhtmlx-gantt</code> 商业许可证（GPL 协议，商用需购买）或考虑开源替代方案 <code>gantt-task-react</code>。
        </p>
      </div>
    </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { getGanttData } from '@/api/report'
import type { GanttTask } from '@/types/report'

const props = defineProps<{ projectId?: number }>()

const hasProject = computed(() => !!props.projectId && props.projectId > 0)
const loading = ref(false)
const ganttLib = ref<'vue-ganttastic' | 'dhtmlx-gantt'>('vue-ganttastic')
const tasks = ref<GanttTask[]>([])
const dhtmlxContainer = ref<HTMLElement | null>(null)
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- dhtmlx-gantt 缺少类型定义
let ganttInstance: any = null

interface GanttBar {
  id: number
  title: string
  start: string
  end: string
  ganttBarConfig?: { color: string; backgroundColor: string; borderRadius: number }
}

const ganttBars = computed<GanttBar[]>(() =>
  tasks.value
    .filter((t) => t.start_date && t.end_date)
    .map((t) => ({
      id: t.id,
      text: t.text,
      title: t.text,
      start: t.start_date,
      end: t.end_date,
      ganttBarConfig: {
        color: '#fff',
        backgroundColor: t.progress >= 1 ? '#67c23a' : t.progress > 0 ? '#e6a23c' : '#909399',
        borderRadius: 4,
      },
    })),
)

const chartStart = computed(() => {
  if (ganttBars.value.length === 0) return '2026-01-01'
  const dates = ganttBars.value.map((t) => new Date(t.start).getTime())
  return new Date(Math.min(...dates) - 7 * 86400000).toISOString().split('T')[0]
})

const chartEnd = computed(() => {
  if (ganttBars.value.length === 0) return '2026-12-31'
  const dates = ganttBars.value.map((t) => new Date(t.end).getTime())
  return new Date(Math.max(...dates) + 7 * 86400000).toISOString().split('T')[0]
})

const comparisonData = [
  { dimension: '包大小', vueGanttastic: '~30KB', dhtmlxGantt: '~600KB' },
  { dimension: 'Vue 集成', vueGanttastic: '原生 Vue 3 组件', dhtmlxGantt: '需封装或动态导入' },
  { dimension: '许可证', vueGanttastic: 'MIT（免费商用）', dhtmlxGantt: 'GPL（商用需购买）' },
  { dimension: '拖拽支持', vueGanttastic: '基础拖拽', dhtmlxGantt: '完整拖拽 + 缩放' },
  { dimension: '关键路径', vueGanttastic: '不支持', dhtmlxGantt: '支持' },
  { dimension: '资源视图', vueGanttastic: '不支持', dhtmlxGantt: '支持' },
  { dimension: '时间刻度', vueGanttastic: '日/周/月', dhtmlxGantt: '时/日/周/月/季/年' },
  { dimension: '移动端', vueGanttastic: '基础支持', dhtmlxGantt: '完整触控支持' },
]

async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getGanttData(props.projectId)
    tasks.value = data.tasks || []
    if (ganttLib.value === 'dhtmlx-gantt') {
      await nextTick()
      initDhtmlxGantt()
    }
  } catch { /* handled */ } finally { loading.value = false }
}

async function initDhtmlxGantt() {
  if (!dhtmlxContainer.value || tasks.value.length === 0) return
  try {
    const gantt = await import('dhtmlx-gantt')
    await import('dhtmlx-gantt/codebase/dhtmlxgantt.css')
    ganttInstance = gantt.default || gantt.gantt
    ganttInstance.init(dhtmlxContainer.value)
    ganttInstance.clearAll()
    ganttInstance.parse({
      data: tasks.value.map((t) => ({
        id: t.id,
        text: t.text,
        start_date: t.start_date,
        end_date: t.end_date,
        progress: t.progress,
        parent: t.parent || 0,
      })),
      links: [],
    })
  } catch (err) {
    console.error('dhtmlx-gantt 加载失败:', err)
  }
}

watch(ganttLib, async (val) => {
  if (val === 'dhtmlx-gantt') {
    await nextTick()
    initDhtmlxGantt()
  }
})

onMounted(() => { loadData() })

onBeforeUnmount(() => {
  if (ganttInstance) {
    ganttInstance.destructor()
    ganttInstance = null
  }
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.gantt-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: auto;
  max-height: 500px;
}

.dhtmlx-wrapper {
  width: 100%;
  height: 450px;
}
</style>
