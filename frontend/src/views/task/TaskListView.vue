<template>
  <div class="task-list">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>
    <template v-else>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索任务标题"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="待办" value="todo" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="done" />
        </el-select>
      </div>
      <el-button v-permission="'task:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建任务
      </el-button>
    </div>

    <!-- 视图切换：列表 / WBS 树 / 甘特图 -->
    <el-radio-group v-model="viewMode" size="small" style="margin-bottom: 12px">
      <el-radio-button value="list">列表视图</el-radio-button>
      <el-radio-button value="wbs">WBS 树</el-radio-button>
      <el-radio-button value="gantt">甘特图</el-radio-button>
    </el-radio-group>

    <!-- 列表视图 -->
    <el-table v-if="viewMode === 'list'" v-loading="loading" :data="taskList" stripe>
      <el-table-column prop="wbsCode" label="WBS" width="100" />
      <el-table-column prop="title" label="任务标题" min-width="220">
        <template #default="{ row }">
          <span class="task-title" @click="handleViewDetail(row as Task)">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ typeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="80">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">{{ row.priority }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="进度" width="120">
        <template #default="{ row }">
          <el-progress :percentage="Number(row.completionRate)" :stroke-width="6" />
        </template>
      </el-table-column>
      <el-table-column prop="plannedStart" label="开始" width="110" />
      <el-table-column prop="plannedEnd" label="结束" width="110" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'task:edit'" link type="primary" @click="handleEdit(row as Task)">编辑</el-button>
          <el-button v-permission="'task:edit'" link type="success" @click="handleProgressUpdate(row as Task)">进度</el-button>
          <el-button v-permission="'task:delete'" link type="danger" @click="handleDelete(row as Task)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- WBS 树视图 -->
    <el-tree
      v-if="viewMode === 'wbs'"
      v-loading="wbsLoading"
      :data="wbsTree"
      node-key="id"
      default-expand-all
      :props="{ children: 'children', label: 'title' }"
    >
      <template #default="{ data }">
        <div class="wbs-node">
          <span class="wbs-code">{{ data.wbsCode }}</span>
          <span class="wbs-title">{{ data.title }}</span>
          <el-tag :type="statusType(data.status)" size="small" style="margin-left: 8px">
            {{ statusLabel(data.status) }}
          </el-tag>
          <el-progress
            :percentage="Number(data.completionRate)"
            :stroke-width="4"
            style="width: 100px; margin-left: 12px"
          />
        </div>
      </template>
    </el-tree>

    <!-- 甘特图视图 -->
    <GanttChart v-if="viewMode === 'gantt'" :tasks="taskList" />

    <div v-if="viewMode === 'list'" class="pagination-wrapper">
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

    <!-- 新建/编辑任务对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新建任务'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="formData.type" placeholder="请选择任务类型">
            <el-option label="开发" value="dev" />
            <el-option label="测试" value="test" />
            <el-option label="部署" value="deploy" />
            <el-option label="培训" value="training" />
            <el-option label="设计" value="design" />
            <el-option label="评审" value="review" />
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
        <el-form-item label="计划时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 进度更新对话框 -->
    <el-dialog v-model="progressDialogVisible" title="更新进度" width="360px">
      <el-form label-width="60px">
        <el-form-item label="进度">
          <el-slider v-model="progressValue" :step="5" show-input />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleProgressSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 任务详情抽屉 -->
    <TaskDetailDrawer v-model="detailDrawerVisible" :task-id="detailTaskId" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getTaskList,
  createTask,
  updateTask,
  updateTaskProgress,
  deleteTask,
  getWbsTree,
} from '@/api/task'
import type { Task, TaskCreateRequest } from '@/types/task'
import { TASK_TYPE_LABEL, TASK_STATUS_LABEL, TASK_STATUS_TYPE, PRIORITY_TYPE, labelFrom, tagType } from '@/constants'
import GanttChart from './components/GanttChart.vue'
import TaskDetailDrawer from './components/TaskDetailDrawer.vue'

const props = defineProps<{ projectId?: number }>()
const hasProject = computed(() => !!props.projectId && props.projectId > 0)

const viewMode = ref<'list' | 'wbs' | 'gantt'>('list')
const loading = ref(false)
const wbsLoading = ref(false)
const taskList = ref<Task[]>([])
const wbsTree = ref<Task[]>([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  size: 10,
  keyword: '',
  status: '',
})

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)

// 详情抽屉
const detailDrawerVisible = ref(false)
const detailTaskId = ref<number | null>(null)
const dateRange = ref<[string, string] | null>(null)
const formData = reactive<TaskCreateRequest>({
  title: '',
  description: '',
  parentTaskId: undefined,
  requirementId: undefined,
  type: 'dev',
  priority: 'medium',
  plannedStart: '',
  plannedEnd: '',
})
const formRules: FormRules = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
}

// 进度更新
const progressDialogVisible = ref(false)
const progressTaskId = ref<number | null>(null)
const progressValue = ref(0)

// 映射（来自 @/constants）
function typeLabel(t: string) { return labelFrom(TASK_TYPE_LABEL, t) }
function statusLabel(s: string) { return labelFrom(TASK_STATUS_LABEL, s) }
function statusType(s: string) { return tagType(TASK_STATUS_TYPE, s) }
function priorityType(p: string) { return tagType(PRIORITY_TYPE, p) }

async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getTaskList(props.projectId, queryParams)
    taskList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

async function loadWbs() {
  wbsLoading.value = true
  try {
    const data = await getWbsTree(props.projectId)
    wbsTree.value = data
  } catch { /* handled */ } finally {
    wbsLoading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

watch(viewMode, (mode) => {
  if (mode === 'wbs' && wbsTree.value.length === 0) loadWbs()
})

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', description: '', parentTaskId: undefined, requirementId: undefined, type: 'dev', priority: 'medium', plannedStart: '', plannedEnd: '' })
  dateRange.value = null
  dialogVisible.value = true
}

function handleEdit(row: Task) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    title: row.title,
    description: row.description,
    type: row.type,
    priority: row.priority,
    plannedStart: row.plannedStart,
    plannedEnd: row.plannedEnd,
  })
  if (row.plannedStart && row.plannedEnd) dateRange.value = [row.plannedStart, row.plannedEnd]
  dialogVisible.value = true
}

function handleDateChange(val: [string, string] | null) {
  formData.plannedStart = val?.[0] ?? ''
  formData.plannedEnd = val?.[1] ?? ''
}

function handleViewDetail(row: Task) {
  detailTaskId.value = row.id
  detailDrawerVisible.value = true
}

function handleProgressUpdate(row: Task) {
  progressTaskId.value = row.id
  progressValue.value = Number(row.completionRate)
  progressDialogVisible.value = true
}

async function handleProgressSubmit() {
  if (!progressTaskId.value) return
  try {
    await updateTaskProgress(progressTaskId.value, { completionRate: progressValue.value })
    ElMessage.success('进度更新成功')
    progressDialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

async function handleDelete(row: Task) {
  try {
    await ElMessageBox.confirm(`确定删除任务「${row.title}」？`, '提示', { type: 'warning' })
    await deleteTask(row.id)
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
      await updateTask(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      if (!props.projectId) {
        ElMessage.warning('请先选择一个项目')
        return
      }
      await createTask(props.projectId, formData)
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

.task-title {
  color: #409eff;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.wbs-node {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  padding: 4px 0;

  .wbs-code {
    color: #909399;
    font-size: 12px;
    min-width: 60px;
  }

  .wbs-title {
    flex: 1;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
