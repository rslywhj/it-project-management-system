<template>
  <div class="requirement-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索需求标题"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="草稿" value="draft" />
          <el-option label="评审中" value="reviewing" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
          <el-option label="已排期" value="scheduled" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="done" />
        </el-select>
        <el-select v-model="queryParams.priority" placeholder="优先级" clearable style="width: 120px" @change="handleSearch">
          <el-option label="紧急" value="critical" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
        </el-select>
      </div>
      <el-button v-permission="'requirement:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建需求
      </el-button>
    </div>

    <el-table v-loading="loading" :data="requirementList" stripe>
      <el-table-column prop="title" label="需求标题" min-width="240">
        <template #default="{ row }">
          <span class="req-title" @click="handleViewDetail(row as Requirement)">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="90">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">
            {{ priorityLabel(row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="source" label="来源" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'requirement:edit'" link type="primary" @click="handleEdit(row as Requirement)">编辑</el-button>
          <el-button v-permission="'requirement:manage'" link type="warning" @click="handleStatusChange(row as Requirement)">流转</el-button>
          <el-button v-permission="'requirement:delete'" link type="danger" @click="handleDelete(row as Requirement)">删除</el-button>
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

    <!-- 新建/编辑需求对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑需求' : '新建需求'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入需求标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入需求描述" />
        </el-form-item>
        <el-form-item label="验收标准">
          <el-input v-model="formData.acceptanceCriteria" type="textarea" :rows="2" placeholder="请输入验收标准" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="formData.priority" placeholder="请选择优先级">
            <el-option label="紧急" value="critical" />
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="formData.category" placeholder="如：功能需求、缺陷修复" />
        </el-form-item>
        <el-form-item label="来源">
          <el-input v-model="formData.source" placeholder="如：用户反馈、产品经理" />
        </el-form-item>
        <el-form-item label="关联里程碑">
          <el-input-number v-model="formData.milestoneId" :min="1" :max="999999" placeholder="里程碑ID" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预估工时">
          <el-input-number v-model="formData.estimatedHours" :min="0" :max="9999" :step="4" placeholder="小时" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 状态流转对话框 -->
    <el-dialog v-model="statusDialogVisible" title="需求状态流转" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="statusType(currentRequirement?.status ?? '')">
            {{ statusLabel(currentRequirement?.status ?? '') }}
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

    <!-- 需求详情抽屉 -->
    <RequirementDetailDrawer v-model="detailDrawerVisible" :requirement-id="detailRequirementId" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getRequirementList,
  createRequirement,
  updateRequirement,
  updateRequirementStatus,
  deleteRequirement,
} from '@/api/requirement'
import { formatDate } from '@/utils'
import type { Requirement, RequirementCreateRequest, RequirementStatus } from '@/types/requirement'
import RequirementDetailDrawer from './components/RequirementDetailDrawer.vue'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const requirementList = ref<Requirement[]>([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: '',
  priority: '',
})

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

// 详情抽屉
const detailDrawerVisible = ref(false)
const detailRequirementId = ref<number | null>(null)
const formData = reactive<RequirementCreateRequest>({
  title: '',
  description: '',
  acceptanceCriteria: '',
  priority: 'medium',
  category: '',
  source: '',
  milestoneId: undefined,
  estimatedHours: undefined,
})
const formRules: FormRules = {
  title: [{ required: true, message: '请输入需求标题', trigger: 'blur' }],
}

// 状态流转
const statusDialogVisible = ref(false)
const currentRequirement = ref<Requirement | null>(null)
const nextStatus = ref<RequirementStatus>('reviewing')
const statusComment = ref('')

// 状态流转规则
const transitionMap: Record<string, RequirementStatus[]> = {
  draft: ['reviewing'],
  reviewing: ['approved', 'rejected'],
  approved: ['scheduled'],
  rejected: ['draft'],
  scheduled: ['in_progress'],
  in_progress: ['done'],
  done: [],
}

const availableTransitions = ref<RequirementStatus[]>([])

// 映射
const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const statusLabelMap: Record<string, string> = {
  draft: '草稿', reviewing: '评审中', approved: '已通过', rejected: '已驳回', scheduled: '已排期', in_progress: '进行中', done: '已完成',
}
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  draft: 'info', reviewing: 'warning', approved: 'success', rejected: 'danger', scheduled: 'info', in_progress: 'warning', done: 'success',
}

function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadData() {
  loading.value = true
  try {
    const data = await getRequirementList(props.projectId, queryParams)
    requirementList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', description: '', priority: 'medium', category: '', source: '', milestoneId: undefined, acceptanceCriteria: '', estimatedHours: undefined })
  dialogVisible.value = true
}

function handleEdit(row: Requirement) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    title: row.title,
    description: row.description,
    acceptanceCriteria: row.acceptanceCriteria,
    priority: row.priority,
    category: row.category,
    source: row.source,
    milestoneId: row.milestoneId,
    estimatedHours: row.estimatedHours,
  })
  dialogVisible.value = true
}

function handleViewDetail(row: Requirement) {
  detailRequirementId.value = row.id
  detailDrawerVisible.value = true
}

function handleStatusChange(row: Requirement) {
  currentRequirement.value = row
  availableTransitions.value = transitionMap[row.status] ?? []
  nextStatus.value = availableTransitions.value[0] ?? 'reviewing'
  statusComment.value = ''
  statusDialogVisible.value = true
}

async function handleStatusSubmit() {
  if (!currentRequirement.value) return
  try {
    await updateRequirementStatus(currentRequirement.value.id, {
      targetStatus: nextStatus.value,
      remark: statusComment.value,
    })
    ElMessage.success('状态流转成功')
    statusDialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

async function handleDelete(row: Requirement) {
  try {
    await ElMessageBox.confirm(`确定删除需求「${row.title}」？`, '提示', { type: 'warning' })
    await deleteRequirement(row.id)
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
      await updateRequirement(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createRequirement(props.projectId, formData)
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

.req-title {
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
