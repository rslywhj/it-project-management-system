<template>
  <div class="test-report" v-loading="loading">
    <el-descriptions :column="2" border size="small" style="margin-bottom: 16px">
      <el-descriptions-item label="总用例数">{{ report.totalCases }}</el-descriptions-item>
      <el-descriptions-item label="已执行">{{ report.executedCases }}</el-descriptions-item>
      <el-descriptions-item label="通过">
        <span style="color: #67c23a">{{ report.passCases }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="失败">
        <span style="color: #f56c6c">{{ report.failCases }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="通过率" :span="2">
        <el-progress :percentage="Number(report.passRate)" :stroke-width="12" :status="Number(report.passRate) >= 80 ? 'success' : Number(report.passRate) >= 60 ? 'warning' : 'exception'" />
      </el-descriptions-item>
    </el-descriptions>

    <h4 style="margin: 16px 0 12px">缺陷统计</h4>
    <el-row :gutter="12">
      <el-col :span="6">
        <el-card shadow="hover" class="defect-stat">
          <div class="value">{{ report.defectStats.total }}</div>
          <div class="label">总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="defect-stat warning">
          <div class="value">{{ report.defectStats.open }}</div>
          <div class="label">待处理</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="defect-stat success">
          <div class="value">{{ report.defectStats.resolved }}</div>
          <div class="label">已解决</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="defect-stat info">
          <div class="value">{{ report.defectStats.closed }}</div>
          <div class="label">已关闭</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getTestReport } from '@/api/test'
import type { TestPlan, TestReport } from '@/types/test'

const props = defineProps<{ plan: TestPlan }>()

const loading = ref(false)
const report = reactive<TestReport>({
  testPlanId: 0,
  testPlanName: '',
  totalCases: 0,
  executedCases: 0,
  passCases: 0,
  failCases: 0,
  blockedCases: 0,
  passRate: 0,
  defectStats: { total: 0, open: 0, resolved: 0, closed: 0 },
})

async function loadReport() {
  loading.value = true
  try {
    const data = await getTestReport(props.plan.id)
    Object.assign(report, data)
  } catch { /* handled */ } finally { loading.value = false }
}

onMounted(() => { loadReport() })
</script>

<style scoped lang="scss">
.defect-stat {
  text-align: center;
  .value { font-size: 24px; font-weight: 600; color: #303133; }
  .label { margin-top: 4px; color: #909399; font-size: 13px; }
  &.warning .value { color: #e6a23c; }
  &.success .value { color: #67c23a; }
  &.info .value { color: #909399; }
}
</style>
