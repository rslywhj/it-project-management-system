<template>
  <el-drawer v-model="visible" title="交付物详情" size="520px" destroy-on-close>
    <div v-loading="loading" class="delivery-detail">
      <template v-if="delivery">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="名称">{{ delivery.name }}</el-descriptions-item>
          <el-descriptions-item label="类型">
            <el-tag size="small" type="info">{{ typeLabel(delivery.type) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(delivery.status)" size="small">{{ statusLabel(delivery.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="版本">v{{ delivery.version }}</el-descriptions-item>
          <el-descriptions-item label="文件">{{ delivery.fileName ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="里程碑">{{ delivery.milestoneName ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ delivery.description ?? '无' }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ delivery.submittedByName ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ delivery.reviewedByName ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ delivery.reviewedAt ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核意见">{{ delivery.reviewComment ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ delivery.createdAt }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getDeliveryDetail } from '@/api/delivery'
import type { Delivery } from '@/types/delivery'

const visible = defineModel<boolean>({ default: false })
const props = defineProps<{ deliveryId: number | null }>()

const loading = ref(false)
const delivery = ref<Delivery | null>(null)

const typeMap: Record<string, string> = { document: '文档', code: '代码', test_report: '测试报告', other: '其他' }
const statusLabelMap: Record<string, string> = { draft: '草稿', submitted: '已提交', approved: '已通过', rejected: '已拒绝' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  draft: 'info', submitted: 'warning', approved: 'success', rejected: 'danger',
}

function typeLabel(t: string) { return typeMap[t] ?? t }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string) { return statusTypeMap[s] ?? 'info' }

watch(() => props.deliveryId, async (id) => {
  if (!id || !visible.value) return
  loading.value = true
  try {
    delivery.value = await getDeliveryDetail(id)
  } catch { /* handled */ } finally {
    loading.value = false
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
.delivery-detail {
  padding: 0 4px;
}
</style>
