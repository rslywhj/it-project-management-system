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
import { DELIVERY_TYPE_LABEL, DELIVERY_STATUS_LABEL, DELIVERY_STATUS_TYPE, labelFrom, tagType } from '@/constants'

const visible = defineModel<boolean>({ default: false })
const props = defineProps<{ deliveryId: number | null }>()

const loading = ref(false)
const delivery = ref<Delivery | null>(null)

function typeLabel(t: string) { return labelFrom(DELIVERY_TYPE_LABEL, t) }
function statusLabel(s: string) { return labelFrom(DELIVERY_STATUS_LABEL, s) }
function statusType(s: string) { return tagType(DELIVERY_STATUS_TYPE, s) }

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
