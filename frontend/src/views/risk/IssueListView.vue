<template>
  <div class="issue-list">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索问题标题" prefix-icon="Search" clearable style="width: 200px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 110px" @change="handleSearch">
          <el-option label="待处理" value="open" />
          <el-option label="处理中" value="in_progress" />
          <el-option label="已解决" value="resolved" />
          <el-option label="已关闭" value="closed" />
        </el-select>
        <el-select v-model="queryParams.priority" placeholder="优先级" clearable style="width: 100px" @change="handleSearch">
          <el-option label="紧急" value="critical" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
        </el-select>
      </div>
      <el-button type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建问题
      </el-button>
    </div>

    <el-table v-loading="loading" :data="issueList" stripe>
      <el-table-column prop="title" label="问题标题" min-width="220" />
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="issueStatusType(row.status)" size="small">{{ issueStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assigneeName" label="处理人" width="100" />
      <el-table-column prop="dueDate" label="截止日期" width="110" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row as Issue)">编辑</el-button>
          <el-button v-if="row.status === 'open' || row.status === 'in_progress'" link type="success" @click="handleResolve(row as Issue)">解决</el-button>
          <el-button v-if="row.status === 'resolved'" link type="warning" @click="handleCloseIssue(row as Issue)">关闭</el-button>
          <el-button link type="danger" @click="handleDelete(row as Issue)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑问题' : '新建问题'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="问题标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="问题描述" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="formData.category" placeholder="如：技术问题、需求变更、资源冲突" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-select v-model="formData.priority">
                <el-option label="紧急" value="critical" />
                <el-option label="高" value="high" />
                <el-option label="中" value="medium" />
                <el-option label="低" value="low" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker v-model="formData.dueDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 解决对话框 -->
    <el-dialog v-model="resolveDialogVisible" title="解决问题" width="420px">
      <el-form label-width="60px">
        <el-form-item label="解决方案">
          <el-input v-model="resolution" type="textarea" :rows="3" placeholder="请输入解决方案" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResolveSubmit">确定</el-button>
      </template>
    </el-dialog>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getIssueList, createIssue, updateIssue, deleteIssue, resolveIssue, closeIssue } from '@/api/risk'
import type { Issue, IssueCreateRequest } from '@/types/risk'

const props = defineProps<{ projectId?: number }>()

const hasProject = computed(() => !!props.projectId && props.projectId > 0)
const loading = ref(false)
const issueList = ref<Issue[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '', priority: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<IssueCreateRequest>({ title: '', description: '', category: '', priority: 'medium', dueDate: '' })
const formRules: FormRules = { title: [{ required: true, message: '请输入问题标题', trigger: 'blur' }] }

const resolveDialogVisible = ref(false)
const resolvingId = ref<number | null>(null)
const resolution = ref('')

const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const issueStatusLabelMap: Record<string, string> = { open: '待处理', in_progress: '处理中', resolved: '已解决', closed: '已关闭' }
const issueStatusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = { open: 'danger', in_progress: 'warning', resolved: 'success', closed: 'info' }

function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function issueStatusLabel(s: string) { return issueStatusLabelMap[s] ?? s }
function issueStatusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return issueStatusTypeMap[s] ?? 'info' }

async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getIssueList(props.projectId, queryParams)
    issueList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', description: '', category: '', priority: 'medium', dueDate: '' })
  dialogVisible.value = true
}

function handleEdit(row: Issue) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, { title: row.title, description: row.description, category: row.category, priority: row.priority, dueDate: row.dueDate })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!isEdit.value && !props.projectId) {
    ElMessage.warning('请先选择一个项目')
    return
  }
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateIssue(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createIssue(props.projectId!, formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

function handleResolve(row: Issue) {
  resolvingId.value = row.id
  resolution.value = ''
  resolveDialogVisible.value = true
}

async function handleResolveSubmit() {
  if (!resolvingId.value) return
  try {
    await resolveIssue(resolvingId.value, resolution.value)
    ElMessage.success('问题已解决')
    resolveDialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

async function handleCloseIssue(row: Issue) {
  try {
    await ElMessageBox.confirm(`确定关闭问题「${row.title}」？`, '提示', { type: 'warning' })
    await closeIssue(row.id)
    ElMessage.success('问题已关闭')
    loadData()
  } catch { /* cancelled */ }
}

async function handleDelete(row: Issue) {
  try {
    await ElMessageBox.confirm(`确定删除问题「${row.title}」？`, '提示', { type: 'warning' })
    await deleteIssue(row.id)
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
