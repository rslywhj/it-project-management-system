<template>
  <div class="gantt-chart">
    <div class="gantt-header">
      <h4>甘特图视图</h4>
      <el-radio-group v-model="ganttLib" size="small" @change="handleLibChange">
        <el-radio-button value="vue-ganttastic">vue-ganttastic</el-radio-button>
        <el-radio-button value="dhtmlx-gantt">dhtmlx-gantt</el-radio-button>
      </el-radio-group>
    </div>

    <!-- vue-ganttastic 实现 -->
    <div v-if="ganttLib === 'vue-ganttastic'" class="gantt-container">
      <g-gantt-chart
        :chart-start="chartStart"
        :chart-end="chartEnd"
        precision="day"
        bar-start="start"
        bar-end="end"
        date-format="YYYY-MM-DD"
        :row-height="40"
        :grid-width="gridWidth"
        :grid-step="gridStep"
        @click-bar="handleBarClick"
      >
        <g-gantt-row
          v-for="task in ganttTasks"
          :key="task.id"
          :label="task.title"
          :bars="[task]"
          :highlight-on-hover="true"
        />
      </g-gantt-chart>
    </div>

    <!-- dhtmlx-gantt 实现 -->
    <div v-if="ganttLib === 'dhtmlx-gantt'" class="gantt-container">
      <div ref="dhtmlxContainer" class="dhtmlx-gantt-wrapper"></div>
    </div>

    <!-- 无数据提示 -->
    <el-empty v-if="tasks.length === 0" description="暂无任务数据，无法展示甘特图" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount, nextTick } from 'vue'
import type { Task } from '@/types/task'

const props = defineProps<{ tasks: Task[] }>()

const ganttLib = ref<'vue-ganttastic' | 'dhtmlx-gantt'>('vue-ganttastic')
const dhtmlxContainer = ref<HTMLElement | null>(null)
let ganttInstance: any = null

// vue-ganttastic 数据转换
interface GanttBar {
  id: number
  title: string
  start: string
  end: string
  ganttBarConfig?: {
    color?: string
    backgroundColor?: string
    borderRadius?: number
    opacity?: number
  }
}

const ganttTasks = computed<GanttBar[]>(() => {
  return props.tasks
    .filter((t) => t.plannedStart && t.plannedEnd)
    .map((t) => ({
      id: t.id,
      title: t.title,
      start: t.plannedStart!,
      end: t.plannedEnd!,
      ganttBarConfig: {
        color: '#fff',
        backgroundColor: statusColor(t.status),
        borderRadius: 4,
      },
    }))
})

// 图表时间范围
const chartStart = computed(() => {
  if (ganttTasks.value.length === 0) return '2026-01-01'
  const dates = ganttTasks.value.map((t) => new Date(t.start).getTime())
  const min = Math.min(...dates)
  return new Date(min - 7 * 86400000).toISOString().split('T')[0]
})

const chartEnd = computed(() => {
  if (ganttTasks.value.length === 0) return '2026-12-31'
  const dates = ganttTasks.value.map((t) => new Date(t.end).getTime())
  const max = Math.max(...dates)
  return new Date(max + 7 * 86400000).toISOString().split('T')[0]
})

const gridWidth = ref(40)
const gridStep = ref(1)

function statusColor(status: string): string {
  const map: Record<string, string> = {
    todo: '#909399',
    in_progress: '#e6a23c',
    done: '#67c23a',
  }
  return map[status] ?? '#409eff'
}

function handleBarClick({ bar }: { bar: GanttBar }) {
  console.log('Bar clicked:', bar)
}

function handleLibChange() {
  if (ganttLib.value === 'dhtmlx-gantt') {
    nextTick(() => initDhtmlxGantt())
  }
}

async function initDhtmlxGantt() {
  if (!dhtmlxContainer.value) return

  try {
    // 动态导入 dhtmlx-gantt
    const gantt = await import('dhtmlx-gantt')
    await import('dhtmlx-gantt/codebase/dhtmlxgantt.css')

    ganttInstance = gantt.default || gantt.gantt
    ganttInstance.init(dhtmlxContainer.value)
    ganttInstance.clearAll()

    // 转换数据格式
    const ganttData = {
      data: props.tasks.map((t) => ({
        id: t.id,
        text: t.title,
        start_date: t.plannedStart,
        end_date: t.plannedEnd,
        progress: Number(t.completionRate) / 100,
        parent: t.parentTaskId || 0,
      })),
      links: [],
    }

    ganttInstance.parse(ganttData)
  } catch (err) {
    console.error('dhtmlx-gantt 加载失败:', err)
  }
}

onBeforeUnmount(() => {
  if (ganttInstance) {
    ganttInstance.destructor()
    ganttInstance = null
  }
})
</script>

<style scoped lang="scss">
.gantt-chart {
  .gantt-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    h4 {
      margin: 0;
    }
  }

  .gantt-container {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    overflow: auto;
    max-height: 500px;
  }

  .dhtmlx-gantt-wrapper {
    width: 100%;
    height: 450px;
  }
}
</style>
