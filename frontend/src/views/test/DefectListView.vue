<template>
  <div class="bug-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索缺陷标题" prefix-icon="Search" clearable style="width: 220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 110px" @change="handleSearch">
          <el-option label="待处理" value="open" />
          <el-option label="处理中" value="in_progress" />
          <el-option label="已解决" value="resolved" />
          <el-option label="已关闭" value="closed" />
          <el-option label="重新打开" value="reopen" />
        </el-select>
        <el-select v-model="queryParams.severity" placeholder="严重程度" clearable style="width: 110px" @change="handleSearch">
          <el-option label="严重" value="critical" />
          <el-option label="一般" value="major" />
          <el-option label="次要" value="minor" />
          <el-option label="轻微" value="trivial" />
        </el-select>
      </div>
      <el-button v-permission="'test:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建缺陷
      </el-button>
    </div>

    <el-table v-loading="loading" :data="bugList" stripe>
      <el-table-column prop="title" label="缺陷标题" min-width="240" />
      <el-table-column prop="severity" label="严重程度" width="90">
        <template #default="{ row }">
          <el-tag :type="severityType(row.severity)" size="small">{{ severityLabel(row.severity) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="bugStatusType(row.status)" size="small">{{ bugStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'test:edit'" link type="primary" @click="handleEdit(row as Bug)">编辑</el-button>
          <el-button v-if="row.status !== 'closed'" v-permission="'test:manage'" link type="success" @click="handleClose(row as Bug)">关闭</el-button>
          <el-button v-permission="'test:delete'" link type="danger" @click="handleDelete(row as Bug)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑缺陷' : '新建缺陷'" width="580px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="缺陷标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="缺陷描述" />
        </el-form-item>
        <el-form-item label="复现步骤">
          <el-input v-model="formData.stepsToReproduce" type="textarea" :rows="3" placeholder="复现步骤" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="formData.severity">
            <el-option label="严重" value="critical" />
            <el-option label="一般" value="major" />
            <el-option label="次要" value="minor" />
            <el-option label="轻微" value="trivial" />
          </el-select>
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
import { getBugList, createBug, updateBug, updateBugStatus, deleteBug } from '@/api/test'
import type { Bug, BugCreateRequest } from '@/types/test'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const bugList = ref<Bug[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '', severity: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<BugCreateRequest>({
  title: '', description: '', stepsToReproduce: '', severity: 'major', priority: 'medium',
})
const formRules: FormRules = { title: [{ required: true, message: '请输入缺陷标题', trigger: 'blur' }] }

const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const severityMap: Record<string, string> = { critical: '严重', major: '一般', minor: '次要', trivial: '轻微' }
const severityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', major: 'warning', minor: 'info', trivial: 'info' }
const bugStatusLabelMap: Record<string, string> = { open: '待处理', in_progress: '处理中', resolved: '已解决', closed: '已关闭', reopen: '重新打开' }
const bugStatusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  open: 'danger', in_progress: 'warning', resolved: 'success', closed: 'info', reopen: 'danger',
}

function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function severityLabel(s: string) { return severityMap[s] ?? s }
function severityType(s: string): 'danger' | 'warning' | 'info' { return severityTypeMap[s] ?? 'info' }
function bugStatusLabel(s: string) { return bugStatusLabelMap[s] ?? s }
function bugStatusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return bugStatusTypeMap[s] ?? 'info' }

async function loadData() {
  loading.value = true
  try {
    const data = await getBugList(props.projectId, queryParams)
    bugList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', description: '', stepsToReproduce: '', severity: 'major', priority: 'medium' })
  dialogVisible.value = true
}

function handleEdit(row: Bug) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    title: row.title, description: row.description, stepsToReproduce: row.stepsToReproduce,
    severity: row.severity, priority: row.priority,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateBug(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createBug(props.projectId, formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handleClose(row: Bug) {
  try {
    const { value } = await ElMessageBox.prompt('请输入解决方案', '关闭缺陷', {
      inputType: 'textarea',
      inputPlaceholder: '解决方案（可选）',
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    })
    await updateBugStatus(row.id, { status: 'closed', resolution: value || undefined })
    ElMessage.success('缺陷已关闭')
    loadData()
  } catch { /* cancelled */ }
}

async function handleDelete(row: Bug) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.title}」？`, '提示', { type: 'warning' })
    await deleteBug(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>
