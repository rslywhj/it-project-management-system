<template>
  <div class="progress-detail">
    <!-- 单元概览 -->
    <el-descriptions :column="2" border size="small" style="margin-bottom: 16px">
      <el-descriptions-item label="状态">
        <el-tag :type="statusType(unit.status)" size="small">{{ statusLabel(unit.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="整体进度">
        <el-progress :percentage="Number(unit.completionRate)" :stroke-width="10" />
      </el-descriptions-item>
      <el-descriptions-item label="负责人">{{ unit.responsibleUserName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前阶段">{{ unit.currentStageName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="计划周期">{{ unit.expectedStartDate || '-' }} ~ {{ unit.expectedEndDate || '-' }}</el-descriptions-item>
    </el-descriptions>

    <!-- 阶段进度时间线 -->
    <h4 style="margin: 16px 0 12px">阶段进度</h4>
    <el-timeline v-if="progressList.length > 0">
      <el-timeline-item
        v-for="item in progressList"
        :key="item.id"
        :type="stageTimelineType(item.status)"
        :hollow="item.status === 'pending'"
        placement="top"
      >
        <div class="stage-item">
          <div class="stage-header">
            <span class="stage-name">{{ item.stageTemplateName || `阶段 ${item.stageTemplateId}` }}</span>
            <el-tag :type="stageStatusType(item.status)" size="small">{{ stageStatusLabel(item.status) }}</el-tag>
          </div>
          <div class="stage-meta">
            <span v-if="item.startedAt">开始：{{ item.startedAt }}</span>
            <span v-if="item.completedAt">完成：{{ item.completedAt }}</span>
            <span v-if="item.expectedEndDate">预计：{{ item.expectedEndDate }}</span>
          </div>
          <el-progress v-if="item.status !== 'pending'" :percentage="Number(item.completionRate)" :stroke-width="6" style="margin-top: 4px" />
        </div>
      </el-timeline-item>
    </el-timeline>
    <el-empty v-else description="暂无阶段进度数据" />

    <!-- 推进操作 -->
    <div v-if="nextStage" style="margin-top: 16px">
      <el-divider />
      <h4>推进到下一阶段</h4>
      <p style="color: #909399; font-size: 13px; margin: 8px 0">
        当前阶段完成后，可推进到「{{ nextStage.stageTemplateName }}」
      </p>
      <el-button type="primary" @click="handleAdvance">推进阶段</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPromotionProgress, updatePromotionProgress } from '@/api/promotion'
import type { PromotionUnit, PromotionProgress } from '@/types/promotion'

const props = defineProps<{ unit: PromotionUnit }>()

const progressList = ref<PromotionProgress[]>([])

const statusLabelMap: Record<string, string> = { pending: '待启动', in_progress: '进行中', completed: '已完成', suspended: '已暂停' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  pending: 'info', in_progress: 'warning', completed: 'success', suspended: 'danger',
}
const stageStatusLabelMap: Record<string, string> = { pending: '待开始', in_progress: '进行中', completed: '已完成', skipped: '已跳过' }
const stageStatusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  pending: 'info', in_progress: 'warning', completed: 'success', skipped: 'danger',
}

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }
function stageStatusLabel(s: string) { return stageStatusLabelMap[s] ?? s }
function stageStatusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return stageStatusTypeMap[s] ?? 'info' }
function stageTimelineType(s: string): '' | 'primary' | 'success' | 'warning' | 'danger' {
  const map: Record<string, '' | 'primary' | 'success' | 'warning' | 'danger'> = { pending: '', in_progress: 'warning', completed: 'success', skipped: 'danger' }
  return map[s] ?? ''
}

const nextStage = computed(() => {
  return progressList.value.find((p) => p.status === 'pending')
})

async function loadProgress() {
  try {
    const data = await getPromotionProgress(props.unit.id)
    progressList.value = Array.isArray(data) ? data : []
  } catch { /* handled */ }
}

async function handleAdvance() {
  if (!nextStage.value) return
  try {
    await updatePromotionProgress(props.unit.id, nextStage.value.stageTemplateId, {
      status: 'in_progress',
      completionRate: 0,
    })
    ElMessage.success('阶段推进成功')
    loadProgress()
  } catch { /* handled */ }
}

onMounted(() => { loadProgress() })
</script>

<style scoped lang="scss">
.stage-item {
  .stage-header {
    display: flex;
    align-items: center;
    gap: 8px;

    .stage-name {
      font-weight: 500;
    }
  }

  .stage-meta {
    margin-top: 4px;
    color: #909399;
    font-size: 13px;
    display: flex;
    gap: 12px;
  }
}
</style>
