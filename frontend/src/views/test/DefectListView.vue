<template>
  <div class="defect-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索缺陷标题"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="打开" value="open" />
          <el-option label="处理中" value="in_progress" />
          <el-option label="已解决" value="resolved" />
          <el-option label="已关闭" value="closed" />
          <el-option label="重新打开" value="reopened" />
        </el-select>
        <el-select v-model="queryParams.severity" placeholder="严重程度" clearable style="width: 120px" @change="handleSearch">
          <el-option label="致命" value="critical" />
          <el-option label="严重" value="major" />
          <el-option label="一般" value="minor" />
          <el-option label="轻微" value="trivial" />
        </el-select>
      </div>
      <el-button v-permission="'test:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>提交缺陷
      </el-button>
    </div>

    <el-table v-loading="loading" :data="defectList" stripe>
      <el-table-column prop="title" label="缺陷标题" min-width="240">
        <template #default="{ row }">
          <span class="defect-title" @click="handleViewDetail(row as Defect)">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="severity" label="严重程度" width="100">
        <template #default="{ row }">
          <el-tag :type="severityType(row.severity)" size="small">{{ severityLabel(row.severity) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="90">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assigneeName" label="处理人" width="100" />
      <el-table-column prop="reporterName" label="报告人" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'test:manage'" link type="warning" @click="handleStatusChange(row as Defect)">流转</el-button>
          <el-button v-permission="'test:edit'" link type="primary" @click="handleEdit(row as Defect)">编辑</el-button>
          <el-button v-permission="'test:delete'" link type="danger" @click="handleDelete(row as Defect)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>

    <!-- 新建/编辑缺陷对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑缺陷' : '提交缺陷'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="缺陷标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入缺陷标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="4" placeholder="请描述缺陷现象、复现步骤等" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="formData.severity" placeholder="请选择严重程度">
            <el-option label="致命" value="critical" />
            <el-option label="严重" value="major" />
            <el-option label="一般" value="minor" />
            <el-option label="轻微" value="trivial" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="formData.priority" placeholder="请选择优先级">
            <el-option label="紧急" value="critical" />
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联计划">
          <el-select v-model="formData.planId" placeholder="选择测试计划" clearable style="width: 100%">
            <el-option v-for="plan in planOptions" :key="plan.id" :label="plan.name" :value="plan.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理人">
          <el-input v-model="formData.assignee" placeholder="请输入处理人用户ID（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态流转对话框 -->
    <el-dialog v-model="statusDialogVisible" title="缺陷状态流转" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="statusType(currentDefect?.status ?? '')">
            {{ statusLabel(currentDefect?.status ?? '') }}
          </el-tag>
        </el-form-item>
        <el-form-item label="流转到">
          <el-select v-model="nextStatus" placeholder="选择目标状态">
            <el-option
              v-for="s in availableTransitions"
              :key="s"
              :label="statusLabel(s)"
              :value="s"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="statusComment" type="textarea" :rows="2" placeholder="流转说明（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getDefectList,
  createDefect,
  updateDefect,
  updateDefectStatus,
  deleteDefect,
  getTestPlanList,
} from '@/api/test'
import { formatDate } from '@/utils'
import type {
  Defect,
  DefectCreateRequest,
  DefectStatus,
  DefectStatusUpdateRequest,
  TestPlan,
} from '@/types/test'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const defectList = ref<Defect[]>([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: '',
  severity: '',
})

// 计划选项
const planOptions = ref<TestPlan[]>([])

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<DefectCreateRequest & { assignee?: number }>({
  planId: undefined,
  caseId: undefined,
  title: '',
  description: '',
  severity: 'major',
  priority: 'medium',
  assignee: undefined,
})
const formRules: FormRules = {
  title: [{ required: true, message: '请输入缺陷标题', trigger: 'blur' }],
}

// 状态流转
const statusDialogVisible = ref(false)
const currentDefect = ref<Defect | null>(null)
const nextStatus = ref<DefectStatus>('in_progress')
const statusComment = ref('')

const transitionMap: Record<string, DefectStatus[]> = {
  open: ['in_progress'],
  in_progress: ['resolved', 'open'],
  resolved: ['closed', 'reopened'],
  closed: ['reopened'],
  reopened: ['in_progress'],
}

const availableTransitions = ref<DefectStatus[]>([])

// 映射
const severityMap: Record<string, string> = { critical: '致命', major: '严重', minor: '一般', trivial: '轻微' }
const severityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', major: 'warning', minor: 'info', trivial: 'info' }
const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const statusLabelMap: Record<string, string> = {
  open: '打开', in_progress: '处理中', resolved: '已解决', closed: '已关闭', reopened: '重新打开',
}
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  open: 'info', in_progress: 'warning', resolved: 'success', closed: 'success', reopened: 'danger',
}

function severityLabel(s: string) { return severityMap[s] ?? s }
function severityType(s: string): 'danger' | 'warning' | 'info' { return severityTypeMap[s] ?? 'info' }
function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadPlanOptions() {
  try {
    const data = await getTestPlanList(props.projectId, { page: 1, size: 100 })
    planOptions.value = data.records
  } catch { /* handled */ }
}

async function loadData() {
  loading.value = true
  try {
    const data = await getDefectList(props.projectId, queryParams)
    defectList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, {
    planId: undefined,
    caseId: undefined,
    title: '',
    description: '',
    severity: 'major',
    priority: 'medium',
    assignee: undefined,
  })
  dialogVisible.value = true
}

function handleEdit(row: Defect) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    planId: row.planId,
    caseId: row.caseId,
    title: row.title,
    description: row.description,
    severity: row.severity,
    priority: row.priority,
    assignee: row.assignee,
  })
  dialogVisible.value = true
}

function handleViewDetail(row: Defect) {
  ElMessageBox.alert(
    `<p><b>标题：</b>${row.title}</p>
     <p><b>描述：</b>${row.description || '无'}</p>
     <p><b>严重程度：</b>${severityLabel(row.severity)}</p>
     <p><b>状态：</b>${statusLabel(row.status)}</p>
     <p><b>处理人：</b>${row.assigneeName || '未指派'}</p>
     <p><b>报告人：</b>${row.reporterName || '-'}</p>
     <p><b>创建时间：</b>${formatDate(row.createdAt, 'YYYY-MM-DD HH:mm:ss')}</p>`,
    '缺陷详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '关闭' },
  )
}

function handleStatusChange(row: Defect) {
  currentDefect.value = row
  availableTransitions.value = transitionMap[row.status] ?? []
  nextStatus.value = availableTransitions.value[0] ?? 'in_progress'
  statusComment.value = ''
  statusDialogVisible.value = true
}

async function handleStatusSubmit() {
  if (!currentDefect.value) return
  try {
    const data: DefectStatusUpdateRequest = {
      targetStatus: nextStatus.value,
      remark: statusComment.value,
    }
    await updateDefectStatus(currentDefect.value.id, data)
    ElMessage.success('状态流转成功')
    statusDialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

async function handleDelete(row: Defect) {
  try {
    await ElMessageBox.confirm(`确定删除缺陷「${row.title}」？`, '提示', { type: 'warning' })
    await deleteDefect(row.id)
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
      await updateDefect(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createDefect(props.projectId, formData)
      ElMessage.success('提交成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadPlanOptions()
  loadData()
})
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .toolbar-left {
    display: flex;
    gap: 8px;
  }
}

.defect-title {
  color: #409eff;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
