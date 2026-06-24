<template>
  <el-drawer v-model="visible" title="需求详情" size="520px" destroy-on-close>
    <div v-loading="loading" class="requirement-detail">
      <template v-if="requirement">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="标题">{{ requirement.title }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(requirement.status)" size="small">{{ statusLabel(requirement.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="priorityType(requirement.priority)" size="small">{{ requirement.priority }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="来源">{{ requirement.source ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ requirement.category ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="预估工时">{{ requirement.estimatedHours ? `${requirement.estimatedHours}h` : '-' }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ requirement.description ?? '无' }}</el-descriptions-item>
          <el-descriptions-item label="验收标准">{{ requirement.acceptanceCriteria ?? '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ requirement.createdAt }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ requirement.updatedAt }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getRequirementDetail } from '@/api/requirement'
import type { Requirement } from '@/types/requirement'
import { REQUIREMENT_STATUS_LABEL, REQUIREMENT_STATUS_TYPE, PRIORITY_TYPE, labelFrom, tagType } from '@/constants'

const visible = defineModel<boolean>({ default: false })
const props = defineProps<{ requirementId: number | null }>()

const loading = ref(false)
const requirement = ref<Requirement | null>(null)

function statusLabel(s: string) { return labelFrom(REQUIREMENT_STATUS_LABEL, s) }
function statusType(s: string) { return tagType(REQUIREMENT_STATUS_TYPE, s) }
function priorityType(p: string) { return tagType(PRIORITY_TYPE, p) }

watch(() => props.requirementId, async (id) => {
  if (!id || !visible.value) return
  loading.value = true
  try {
    requirement.value = await getRequirementDetail(id)
  } catch { /* handled */ } finally {
    loading.value = false
  }
}, { immediate: true })
</script>

<style scoped lang="scss">
.requirement-detail {
  padding: 0 4px;
}
</style>
