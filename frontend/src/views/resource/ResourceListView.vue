<template>
  <div class="resource-list">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>
    <template v-else>
    <el-tabs v-model="activeTab">
      <!-- 资源分配 -->
      <el-tab-pane label="资源分配" name="allocation">
        <div class="toolbar">
          <el-button v-permission="'resource:create'" type="primary" size="small" @click="handleCreateAllocation">
            <el-icon><Plus /></el-icon>分配资源
          </el-button>
        </div>

        <el-table v-loading="allocationLoading" :data="allocationList" stripe>
          <el-table-column prop="userName" label="成员" width="120" />
          <el-table-column prop="role" label="角色" width="120" />
          <el-table-column label="分配比例" width="140">
            <template #default="{ row }">
              <el-progress :percentage="row.allocationPercent" :stroke-width="10" :status="row.allocationPercent > 100 ? 'exception' : undefined" />
            </template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始" width="110" />
          <el-table-column prop="endDate" label="结束" width="110" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="allocStatusType(row.status)" size="small">{{ allocStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button link type="danger" @click="handleDeleteAllocation(row as ResourceAllocation)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 资源利用率 -->
        <h4 style="margin: 24px 0 12px">资源利用率</h4>
        <el-table v-loading="utilLoading" :data="utilizationList" stripe size="small">
          <el-table-column prop="userName" label="成员" width="120" />
          <el-table-column prop="activeProjects" label="参与项目" width="100" align="center" />
          <el-table-column label="分配比例" width="140">
            <template #default="{ row }">
              <el-progress :percentage="Math.min(row.totalAllocation, 100)" :stroke-width="10" :status="row.totalAllocation > 100 ? 'exception' : (row.totalAllocation >= 80 ? 'success' : undefined)" />
            </template>
          </el-table-column>
          <el-table-column prop="totalHoursThisMonth" label="本月工时" width="100" align="center" />
          <el-table-column label="利用率" width="140">
            <template #default="{ row }">
              <el-progress :percentage="Math.round(row.utilizationRate * 100)" :stroke-width="10" />
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 工时记录 -->
      <el-tab-pane label="工时记录" name="timesheet">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-date-picker v-model="timesheetDateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 260px" @change="loadTimesheets" />
          </div>
          <el-button type="primary" size="small" @click="handleCreateTimesheet">
            <el-icon><Plus /></el-icon>记录工时
          </el-button>
        </div>

        <el-table v-loading="timesheetLoading" :data="timesheetList" stripe>
          <el-table-column prop="userName" label="成员" width="100" />
          <el-table-column prop="taskTitle" label="关联任务" min-width="180" />
          <el-table-column prop="workDate" label="工作日期" width="110" />
          <el-table-column prop="hours" label="工时(h)" width="80" align="center" />
          <el-table-column prop="description" label="工作内容" min-width="200" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="tsStatusType(row.status)" size="small">{{ tsStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 'draft'" link type="primary" @click="handleSubmitTimesheet(row as Timesheet)">提交</el-button>
              <el-button v-if="row.status === 'submitted'" link type="success" @click="handleApproveTimesheet(row as Timesheet)">审批</el-button>
              <el-button v-if="row.status === 'draft'" link type="danger" @click="handleDeleteTimesheet(row as Timesheet)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 分配资源对话框 -->
    <el-dialog v-model="allocDialogVisible" title="分配资源" width="480px" destroy-on-close>
      <el-form ref="allocFormRef" :model="allocFormData" :rules="allocFormRules" label-width="80px">
        <el-form-item label="成员ID" prop="userId">
          <el-input-number v-model="allocFormData.userId" :min="1" placeholder="用户ID" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-input v-model="allocFormData.role" placeholder="如：前端开发、后端开发、测试" />
        </el-form-item>
        <el-form-item label="分配比例">
          <el-slider v-model="allocFormData.allocationPercent" :max="200" :step="10" show-input />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="allocDateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" @change="handleAllocDateChange" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="allocDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="allocSubmitLoading" @click="handleAllocSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 记录工时对话框 -->
    <el-dialog v-model="tsDialogVisible" title="记录工时" width="480px" destroy-on-close>
      <el-form ref="tsFormRef" :model="tsFormData" :rules="tsFormRules" label-width="80px">
        <el-form-item label="工作日期" prop="workDate">
          <el-date-picker v-model="tsFormData.workDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="工时(h)" prop="hours">
          <el-input-number v-model="tsFormData.hours" :min="0.5" :max="24" :step="0.5" />
        </el-form-item>
        <el-form-item label="关联任务">
          <el-input-number v-model="tsFormData.taskId" :min="0" placeholder="任务ID（可选）" />
        </el-form-item>
        <el-form-item label="工作内容">
          <el-input v-model="tsFormData.description" type="textarea" :rows="3" placeholder="工作内容描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tsDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="tsSubmitLoading" @click="handleTsSubmit">确定</el-button>
      </template>
    </el-dialog>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getAllocationList, createAllocation, deleteAllocation, getResourceUtilization, getTimesheetList, createTimesheet, submitTimesheet, approveTimesheet, deleteTimesheet } from '@/api/resource'
import type { ResourceAllocation, ResourceAllocationCreateRequest, Timesheet, TimesheetCreateRequest, ResourceUtilization } from '@/types/resource'

const props = defineProps<{ projectId?: number }>()
const hasProject = computed(() => !!props.projectId && props.projectId > 0)

const activeTab = ref('allocation')

// 资源分配
const allocationLoading = ref(false)
const allocationList = ref<ResourceAllocation[]>([])
const allocDialogVisible = ref(false)
const allocSubmitLoading = ref(false)
const allocFormRef = ref<FormInstance>()
const allocDateRange = ref<[string, string] | null>(null)
const allocFormData = reactive<ResourceAllocationCreateRequest>({ userId: 1, role: '', allocationPercent: 100, startDate: '', endDate: '' })
const allocFormRules: FormRules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  role: [{ required: true, message: '请输入角色', trigger: 'blur' }],
}

// 资源利用率
const utilLoading = ref(false)
const utilizationList = ref<ResourceUtilization[]>([])

// 工时记录
const timesheetLoading = ref(false)
const timesheetList = ref<Timesheet[]>([])
const timesheetDateRange = ref<[string, string] | null>(null)
const tsDialogVisible = ref(false)
const tsSubmitLoading = ref(false)
const tsFormRef = ref<FormInstance>()
const tsFormData = reactive<TimesheetCreateRequest>({ workDate: '', hours: 8, description: '' })
const tsFormRules: FormRules = {
  workDate: [{ required: true, message: '请选择工作日期', trigger: 'change' }],
  hours: [{ required: true, message: '请输入工时', trigger: 'blur' }],
}

const allocStatusMap: Record<string, string> = { planned: '计划中', active: '进行中', completed: '已完成', released: '已释放' }
const allocStatusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = { planned: 'info', active: 'warning', completed: 'success', released: 'danger' }
const tsStatusMap: Record<string, string> = { draft: '草稿', submitted: '已提交', approved: '已审批', rejected: '已驳回' }
const tsStatusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = { draft: 'info', submitted: 'warning', approved: 'success', rejected: 'danger' }

function allocStatusLabel(s: string) { return allocStatusMap[s] ?? s }
function allocStatusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return allocStatusTypeMap[s] ?? 'info' }
function tsStatusLabel(s: string) { return tsStatusMap[s] ?? s }
function tsStatusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return tsStatusTypeMap[s] ?? 'info' }

async function loadAllocations() {
  if (!props.projectId) return
  allocationLoading.value = true
  try {
    const data = await getAllocationList(props.projectId)
    allocationList.value = data.records || []
  } catch { /* handled */ } finally { allocationLoading.value = false }
}

async function loadUtilization() {
  if (!props.projectId) return
  utilLoading.value = true
  try {
    const data = await getResourceUtilization(props.projectId)
    utilizationList.value = Array.isArray(data) ? data : []
  } catch { /* handled */ } finally { utilLoading.value = false }
}

async function loadTimesheets() {
  if (!props.projectId) return
  timesheetLoading.value = true
  try {
    const startDate = timesheetDateRange.value?.[0]
    const endDate = timesheetDateRange.value?.[1]
    const data = await getTimesheetList(props.projectId, { page: 1, size: 50, startDate, endDate })
    timesheetList.value = data.records || []
  } catch { /* handled */ } finally { timesheetLoading.value = false }
}

function handleCreateAllocation() {
  Object.assign(allocFormData, { userId: 1, role: '', allocationPercent: 100, startDate: '', endDate: '' })
  allocDateRange.value = null
  allocDialogVisible.value = true
}

function handleAllocDateChange(val: [string, string] | null) {
  allocFormData.startDate = val?.[0] ?? ''
  allocFormData.endDate = val?.[1] ?? ''
}

async function handleAllocSubmit() {
  const valid = await allocFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!props.projectId) { ElMessage.warning('请先选择一个项目'); return }
  allocSubmitLoading.value = true
  try {
    await createAllocation(props.projectId!, allocFormData)
    ElMessage.success('分配成功')
    allocDialogVisible.value = false
    loadAllocations()
    loadUtilization()
  } catch { /* handled */ } finally { allocSubmitLoading.value = false }
}

async function handleDeleteAllocation(row: ResourceAllocation) {
  try {
    await ElMessageBox.confirm(`确定移除「${row.userName}」的资源分配？`, '提示', { type: 'warning' })
    await deleteAllocation(row.id)
    ElMessage.success('移除成功')
    loadAllocations()
    loadUtilization()
  } catch { /* cancelled */ }
}

function handleCreateTimesheet() {
  Object.assign(tsFormData, { workDate: '', hours: 8, description: '' })
  tsDialogVisible.value = true
}

async function handleTsSubmit() {
  const valid = await tsFormRef.value?.validate().catch(() => false)
  if (!valid) return
  if (!props.projectId) { ElMessage.warning('请先选择一个项目'); return }
  tsSubmitLoading.value = true
  try {
    await createTimesheet(props.projectId!, tsFormData)
    ElMessage.success('记录成功')
    tsDialogVisible.value = false
    loadTimesheets()
  } catch { /* handled */ } finally { tsSubmitLoading.value = false }
}

async function handleSubmitTimesheet(row: Timesheet) {
  try {
    await submitTimesheet(row.id)
    ElMessage.success('提交成功')
    loadTimesheets()
  } catch { /* handled */ }
}

async function handleApproveTimesheet(row: Timesheet) {
  try {
    await approveTimesheet(row.id)
    ElMessage.success('审批通过')
    loadTimesheets()
  } catch { /* handled */ }
}

async function handleDeleteTimesheet(row: Timesheet) {
  try {
    await ElMessageBox.confirm('确定删除该工时记录？', '提示', { type: 'warning' })
    await deleteTimesheet(row.id)
    ElMessage.success('删除成功')
    loadTimesheets()
  } catch { /* cancelled */ }
}

onMounted(() => {
  loadAllocations()
  loadUtilization()
  loadTimesheets()
})
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
</style>
