<template>
  <div class="test-plan-list">
    <el-empty v-if="!hasProject" description="请先选择一个项目">
      <el-button type="primary" @click="$router.push('/project/list')">选择项目</el-button>
    </el-empty>

    <template v-else>
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索测试计划" prefix-icon="Search" clearable style="width: 220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="草稿" value="draft" />
          <el-option label="执行中" value="active" />
          <el-option label="已完成" value="completed" />
          <el-option label="已归档" value="archived" />
        </el-select>
      </div>
      <el-button v-permission="'test:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建测试计划
      </el-button>
    </div>

    <el-table v-loading="loading" :data="planList" stripe>
      <el-table-column prop="name" label="测试计划" min-width="220" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="caseCount" label="用例数" width="80" align="center" />
      <el-table-column label="通过率" width="120">
        <template #default="{ row }">
          <el-progress v-if="row.passRate != null" :percentage="Number(row.passRate)" :stroke-width="8" :status="Number(row.passRate) >= 80 ? 'success' : Number(row.passRate) >= 60 ? 'warning' : 'exception'" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="startDate" label="开始" width="110" />
      <el-table-column prop="endDate" label="结束" width="110" />
      <el-table-column prop="createdByName" label="创建人" width="100" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'test:view'" link type="primary" @click="handleViewReport(row as TestPlan)">报告</el-button>
          <el-button v-permission="'test:edit'" link type="warning" @click="handleEdit(row as TestPlan)">编辑</el-button>
          <el-button v-permission="'test:delete'" link type="danger" @click="handleDelete(row as TestPlan)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑测试计划' : '新建测试计划'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="测试计划名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="计划描述" />
        </el-form-item>
        <el-form-item label="计划时间">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD" @change="handleDateChange" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 测试报告抽屉 -->
    <el-drawer v-model="reportDrawerVisible" :title="`${currentPlan?.name} - 测试报告`" size="500px">
      <TestReportDetail v-if="currentPlan" :plan="currentPlan" />
    </el-drawer>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getTestPlanList, createTestPlan, updateTestPlan, deleteTestPlan } from '@/api/test'
import type { TestPlan, TestPlanCreateRequest } from '@/types/test'
import TestReportDetail from './components/TestReportDetail.vue'

const props = defineProps<{ projectId?: number }>()

const loading = ref(false)
const planList = ref<TestPlan[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const dateRange = ref<[string, string] | null>(null)
const formData = reactive<TestPlanCreateRequest>({ name: '', description: '', startDate: '', endDate: '' })
const formRules: FormRules = { name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }] }

const reportDrawerVisible = ref(false)
const currentPlan = ref<TestPlan | null>(null)

const statusLabelMap: Record<string, string> = { draft: '草稿', active: '执行中', completed: '已完成', archived: '已归档' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  draft: 'info', active: 'warning', completed: 'success', archived: 'danger',
}

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadData() {
  if (!props.projectId) return
  loading.value = true
  try {
    const data = await getTestPlanList(props.projectId, queryParams)
    planList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { name: '', description: '', startDate: '', endDate: '' })
  dateRange.value = null
  dialogVisible.value = true
}

function handleEdit(row: TestPlan) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, { name: row.name, description: row.description, startDate: row.startDate, endDate: row.endDate })
  if (row.startDate && row.endDate) dateRange.value = [row.startDate, row.endDate]
  dialogVisible.value = true
}

function handleDateChange(val: [string, string] | null) {
  formData.startDate = val?.[0] ?? ''
  formData.endDate = val?.[1] ?? ''
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateTestPlan(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createTestPlan(props.projectId, formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handleDelete(row: TestPlan) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
    await deleteTestPlan(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

function handleViewReport(row: TestPlan) {
  currentPlan.value = row
  reportDrawerVisible.value = true
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>
