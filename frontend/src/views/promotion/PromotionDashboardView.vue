<template>
  <div class="promotion-dashboard" v-loading="loading">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <!-- 统计卡片 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.totalUnits }}</div>
          <div class="stat-label">推广单元总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card success">
          <div class="stat-value">{{ dashboard.completedUnits }}</div>
          <div class="stat-label">已完成</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card warning">
          <div class="stat-value">{{ dashboard.inProgressUnits }}</div>
          <div class="stat-label">进行中</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card info">
          <div class="stat-value">{{ dashboard.pendingUnits }}</div>
          <div class="stat-label">待启动</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card danger">
          <div class="stat-value">{{ dashboard.delayedUnits }}</div>
          <div class="stat-label">已延期</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ dashboard.averageCompletionRate }}%</div>
          <div class="stat-label">平均完成率</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 各单位进度对比 -->
    <el-card shadow="never">
      <template #header>
        <span>各单位推广进度</span>
      </template>
      <el-table :data="dashboard.unitProgressList" stripe>
        <el-table-column prop="orgName" label="成员单位" min-width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="200">
          <template #default="{ row }">
            <el-progress
              :percentage="Number(row.completionRate)"
              :stroke-width="12"
              :status="row.isDelayed ? 'exception' : (row.status === 'completed' ? 'success' : undefined)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="currentStageName" label="当前阶段" width="140" />
        <el-table-column prop="expectedEndDate" label="计划完成" width="120" />
        <el-table-column label="延期" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isDelayed" type="danger" size="small">延期</el-tag>
            <span v-else style="color: #67c23a">✓</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { getPromotionDashboard } from '@/api/promotion'
import type { PromotionDashboard } from '@/types/promotion'

const props = defineProps<{ projectId?: number }>()

const hasProject = computed(() => !!props.projectId && props.projectId > 0)
const loading = ref(false)
const dashboard = reactive<PromotionDashboard>({
  totalUnits: 0,
  completedUnits: 0,
  inProgressUnits: 0,
  pendingUnits: 0,
  suspendedUnits: 0,
  delayedUnits: 0,
  averageCompletionRate: 0,
  unitProgressList: [],
})

const statusLabelMap: Record<string, string> = { pending: '待启动', in_progress: '进行中', completed: '已完成', suspended: '已暂停' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  pending: 'info', in_progress: 'warning', completed: 'success', suspended: 'danger',
}

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadDashboard() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getPromotionDashboard(props.projectId)
    Object.assign(dashboard, data)
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

onMounted(() => { loadDashboard() })
</script>

<style scoped lang="scss">
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
</style>
