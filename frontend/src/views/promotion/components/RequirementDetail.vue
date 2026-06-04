<template>
  <div class="requirement-detail">
    <div class="toolbar">
      <el-button v-permission="'promotion:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新增需求
      </el-button>
    </div>

    <el-table v-loading="loading" :data="requirementList" stripe size="small">
      <el-table-column prop="title" label="需求标题" min-width="200" />
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }">
          <el-tag :type="row.type === 'differential' ? 'warning' : 'info'" size="small">
            {{ row.type === 'differential' ? '差异化' : '通用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" size="small" @click="handleEdit(row as UnitRequirement)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(row as UnitRequirement)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑需求' : '新增需求'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入需求标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="需求描述" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="formData.type">
            <el-radio value="general">通用</el-radio>
            <el-radio value="differential">差异化</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="formData.priority">
            <el-option label="紧急" value="critical" />
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getUnitRequirementList, createUnitRequirement, updateUnitRequirement, deleteUnitRequirement } from '@/api/promotion'
import type { UnitRequirement, UnitRequirementCreateRequest } from '@/types/promotion'

const props = defineProps<{ unitId: number }>()

const loading = ref(false)
const requirementList = ref<UnitRequirement[]>([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<UnitRequirementCreateRequest>({
  title: '',
  description: '',
  type: 'differential',
  priority: 'medium',
})
const formRules: FormRules = {
  title: [{ required: true, message: '请输入需求标题', trigger: 'blur' }],
}

const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const statusLabelMap: Record<string, string> = { draft: '草稿', reviewing: '评审中', approved: '已通过', scheduled: '已排期', done: '已完成' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  draft: 'info', reviewing: 'warning', approved: 'success', scheduled: 'info', done: 'success',
}

function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadData() {
  loading.value = true
  try {
    const data = await getUnitRequirementList(props.unitId)
    requirementList.value = data.records || []
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', description: '', type: 'differential', priority: 'medium' })
  dialogVisible.value = true
}

function handleEdit(row: any) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, { title: row.title, description: row.description, type: row.type, priority: row.priority })
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确定删除需求「${row.title}」？`, '提示', { type: 'warning' })
    await deleteUnitRequirement(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateUnitRequirement(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createUnitRequirement(props.unitId, formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    submitLoading.value = false
  }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
