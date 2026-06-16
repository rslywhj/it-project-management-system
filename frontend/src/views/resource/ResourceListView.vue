<template>
  <div class="resource-list">
    <el-tabs v-model="activeTab">
      <!-- 资源池 -->
      <el-tab-pane label="资源管理" name="resource">
        <div class="toolbar">
          <el-button v-permission="'resource:create'" type="primary" size="small" @click="handleCreateResource">
            <el-icon><Plus /></el-icon>添加资源
          </el-button>
        </div>

        <el-table v-loading="resourceLoading" :data="resourceList" stripe>
          <el-table-column prop="realName" label="成员" width="120">
            <template #default="{ row }">{{ row.realName || row.username || `用户${row.userId}` }}</template>
          </el-table-column>
          <el-table-column prop="skillTags" label="技能标签" min-width="160" />
          <el-table-column prop="availability" label="可用状态" width="100">
            <template #default="{ row }">
              <el-tag :type="availType(row.availability)" size="small">{{ availLabel(row.availability) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="负载" width="140">
            <template #default="{ row }">
              <el-progress :percentage="row.workloadPercent ?? 0" :stroke-width="10" :status="(row.workloadPercent ?? 0) > 80 ? 'exception' : undefined" />
            </template>
          </el-table-column>
          <el-table-column prop="capacityHoursPerWeek" label="周可用工时" width="110" align="center" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button link type="danger" @click="handleDeleteResource(row)">移除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 工时记录 -->
      <el-tab-pane label="工时记录" name="worklog">
        <div class="toolbar">
          <div class="toolbar-left">
            <el-date-picker v-model="workLogDateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" style="width: 260px" @change="loadWorkLogs" />
          </div>
          <el-button type="primary" size="small" @click="handleCreateWorkLog">
            <el-icon><Plus /></el-icon>记录工时
          </el-button>
        </div>

        <el-table v-loading="workLogLoading" :data="workLogList" stripe>
          <el-table-column prop="taskTitle" label="关联任务" min-width="180" />
          <el-table-column prop="workDate" label="工作日期" width="110" />
          <el-table-column prop="hours" label="工时(h)" width="80" align="center" />
          <el-table-column prop="workType" label="工作类型" width="100">
            <template #default="{ row }">{{ workTypeLabel(row.workType) }}</template>
          </el-table-column>
          <el-table-column prop="description" label="工作内容" min-width="200" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button link type="danger" @click="handleDeleteWorkLog(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 资源负载 -->
      <el-tab-pane label="资源负载" name="workload">
        <el-table v-loading="workloadLoading" :data="workloadReport?.resourceWorkloads ?? []" stripe size="small">
          <el-table-column prop="realName" label="成员" width="120">
            <template #default="{ row }">{{ row.realName || row.username }}</template>
          </el-table-column>
          <el-table-column prop="availability" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="availType(row.availability)" size="small">{{ availLabel(row.availability) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="负载" width="140">
            <template #default="{ row }">
              <el-progress :percentage="row.workloadPercent ?? 0" :stroke-width="10" :status="(row.workloadPercent ?? 0) > 80 ? 'exception' : undefined" />
            </template>
          </el-table-column>
          <el-table-column prop="totalHoursThisWeek" label="本周工时" width="100" align="center" />
          <el-table-column prop="totalHoursThisMonth" label="本月工时" width="100" align="center" />
        </el-table>
        <div v-if="workloadReport?.workHoursSummary" class="workload-summary">
          <span>本周总工时: {{ workloadReport.workHoursSummary.totalHoursThisWeek }}h</span>
          <span>本月总工时: {{ workloadReport.workHoursSummary.totalHoursThisMonth }}h</span>
          <span>人均工时: {{ workloadReport.workHoursSummary.averageHoursPerPerson }}h</span>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 添加资源对话框 -->
    <el-dialog v-model="resourceDialogVisible" title="添加资源" width="480px" destroy-on-close>
      <el-form ref="resourceFormRef" :model="resourceFormData" :rules="resourceFormRules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="resourceFormData.userId" :min="1" placeholder="用户ID" />
        </el-form-item>
        <el-form-item label="技能标签">
          <el-input v-model="resourceFormData.skillTags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="可用状态">
          <el-select v-model="resourceFormData.availability">
            <el-option label="可用" value="available" />
            <el-option label="忙碌" value="busy" />
            <el-option label="不可用" value="unavailable" />
            <el-option label="休假" value="on_leave" />
          </el-select>
        </el-form-item>
        <el-form-item label="周可用工时">
          <el-input-number v-model="resourceFormData.capacityHoursPerWeek" :min="0" :max="80" :step="4" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="resourceFormData.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resourceDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="resourceSubmitLoading" @click="handleResourceSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 记录工时对话框 -->
    <el-dialog v-model="workLogDialogVisible" title="记录工时" width="480px" destroy-on-close>
      <el-form ref="workLogFormRef" :model="workLogFormData" :rules="workLogFormRules" label-width="80px">
        <el-form-item label="工作日期" prop="workDate">
          <el-date-picker v-model="workLogFormData.workDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="工时(h)" prop="hours">
          <el-input-number v-model="workLogFormData.hours" :min="0.5" :max="24" :step="0.5" />
        </el-form-item>
        <el-form-item label="工作类型">
          <el-select v-model="workLogFormData.workType" placeholder="选择类型">
            <el-option label="开发" value="development" />
            <el-option label="测试" value="testing" />
            <el-option label="会议" value="meeting" />
            <el-option label="设计" value="design" />
            <el-option label="评审" value="review" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联任务">
          <el-input-number v-model="workLogFormData.taskId" :min="0" placeholder="任务ID（可选）" />
        </el-form-item>
        <el-form-item label="工作内容">
          <el-input v-model="workLogFormData.description" type="textarea" :rows="3" placeholder="工作内容描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="workLogDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="workLogSubmitLoading" @click="handleWorkLogSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getResourceList, createResource, deleteResource, getWorkLogList, createWorkLog, deleteWorkLog, getWorkloadReport } from '@/api/resource'
import type { Resource, ResourceRequest, WorkLog, WorkLogRequest, WorkloadReport, Availability, WorkType } from '@/types/resource'

const props = defineProps<{ projectId: number }>()

const activeTab = ref('resource')

// 资源池
const resourceLoading = ref(false)
const resourceList = ref<Resource[]>([])
const resourceDialogVisible = ref(false)
const resourceSubmitLoading = ref(false)
const resourceFormRef = ref<FormInstance>()
const resourceFormData = reactive<ResourceRequest>({ userId: 1, availability: 'available', skillTags: '', capacityHoursPerWeek: 40, remark: '' })
const resourceFormRules: FormRules = { userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }] }

const availMap: Record<string, string> = { available: '可用', busy: '忙碌', unavailable: '不可用', on_leave: '休假' }
const availTypeMap: Record<string, 'success' | 'warning' | 'info' | 'danger'> = { available: 'success', busy: 'warning', unavailable: 'info', on_leave: 'danger' }
function availLabel(s: string) { return availMap[s] ?? s }
function availType(s: string): 'success' | 'warning' | 'info' | 'danger' { return availTypeMap[s] ?? 'info' }

const workTypeMap: Record<string, string> = { development: '开发', testing: '测试', meeting: '会议', design: '设计', review: '评审', other: '其他' }
function workTypeLabel(s?: string) { return s ? (workTypeMap[s] ?? s) : '-' }

// 工时记录
const workLogLoading = ref(false)
const workLogList = ref<WorkLog[]>([])
const workLogDateRange = ref<[string, string] | null>(null)
const workLogDialogVisible = ref(false)
const workLogSubmitLoading = ref(false)
const workLogFormRef = ref<FormInstance>()
const workLogFormData = reactive<WorkLogRequest>({ workDate: '', hours: 8, workType: 'development', description: '' })
const workLogFormRules: FormRules = {
  workDate: [{ required: true, message: '请选择工作日期', trigger: 'change' }],
  hours: [{ required: true, message: '请输入工时', trigger: 'blur' }],
}

// 资源负载
const workloadLoading = ref(false)
const workloadReport = ref<WorkloadReport | null>(null)

async function loadResources() {
  resourceLoading.value = true
  try {
    const data = await getResourceList(props.projectId)
    resourceList.value = data.records || []
  } catch { /* handled */ } finally { resourceLoading.value = false }
}

async function loadWorkLogs() {
  workLogLoading.value = true
  try {
    const params: any = {}
    if (workLogDateRange.value) {
      params.startDate = workLogDateRange.value[0]
      params.endDate = workLogDateRange.value[1]
    }
    const data = await getWorkLogList(props.projectId, params)
    workLogList.value = data.records || []
  } catch { /* handled */ } finally { workLogLoading.value = false }
}

async function loadWorkload() {
  workloadLoading.value = true
  try {
    workloadReport.value = await getWorkloadReport(props.projectId)
  } catch { /* handled */ } finally { workloadLoading.value = false }
}

function handleCreateResource() {
  Object.assign(resourceFormData, { userId: 1, availability: 'available', skillTags: '', capacityHoursPerWeek: 40, remark: '' })
  resourceDialogVisible.value = true
}

async function handleResourceSubmit() {
  const valid = await resourceFormRef.value?.validate().catch(() => false)
  if (!valid) return
  resourceSubmitLoading.value = true
  try {
    await createResource(props.projectId, resourceFormData)
    ElMessage.success('添加成功')
    resourceDialogVisible.value = false
    loadResources()
  } catch { /* handled */ } finally { resourceSubmitLoading.value = false }
}

async function handleDeleteResource(row: Resource) {
  try {
    await ElMessageBox.confirm(`确定移除该资源？`, '提示', { type: 'warning' })
    await deleteResource(row.id)
    ElMessage.success('移除成功')
    loadResources()
  } catch { /* cancelled */ }
}

function handleCreateWorkLog() {
  Object.assign(workLogFormData, { workDate: '', hours: 8, workType: 'development', description: '' })
  workLogDialogVisible.value = true
}

async function handleWorkLogSubmit() {
  const valid = await workLogFormRef.value?.validate().catch(() => false)
  if (!valid) return
  workLogSubmitLoading.value = true
  try {
    await createWorkLog(props.projectId, workLogFormData)
    ElMessage.success('记录成功')
    workLogDialogVisible.value = false
    loadWorkLogs()
  } catch { /* handled */ } finally { workLogSubmitLoading.value = false }
}

async function handleDeleteWorkLog(row: WorkLog) {
  try {
    await ElMessageBox.confirm('确定删除该工时记录？', '提示', { type: 'warning' })
    await deleteWorkLog(row.id)
    ElMessage.success('删除成功')
    loadWorkLogs()
  } catch { /* cancelled */ }
}

onMounted(() => {
  loadResources()
  loadWorkLogs()
  loadWorkload()
})
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
.workload-summary { margin-top: 12px; display: flex; gap: 24px; color: #909399; font-size: 13px; }
</style>
