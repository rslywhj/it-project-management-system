<template>
  <div class="test-case-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用例标题"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="loadData"
          @keyup.enter="loadData"
        />
        <el-select v-model="searchPriority" placeholder="优先级" clearable style="width: 120px" @change="loadData">
          <el-option label="紧急" value="critical" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
        </el-select>
        <el-select v-model="searchResult" placeholder="执行结果" clearable style="width: 120px" @change="loadData">
          <el-option label="通过" value="pass" />
          <el-option label="失败" value="fail" />
          <el-option label="阻塞" value="block" />
          <el-option label="跳过" value="skip" />
          <el-option label="未执行" value="pending" />
        </el-select>
      </div>
      <el-button v-permission="'test:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建用例
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="caseTree"
      row-key="id"
      default-expand-all
      :tree-props="{ children: 'children' }"
      stripe
    >
      <el-table-column prop="title" label="用例标题" min-width="280">
        <template #default="{ row }">
          <span class="case-title">{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="90">
        <template #default="{ row }">
          <el-tag :type="priorityType(row.priority)" size="small">{{ priorityLabel(row.priority) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="result" label="执行结果" width="100">
        <template #default="{ row }">
          <el-tag :type="resultType(row.result)" size="small">{{ resultLabel(row.result) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="moduleName" label="模块" width="120" />
      <el-table-column prop="executedByName" label="执行人" width="100" />
      <el-table-column prop="executedAt" label="执行时间" width="170">
        <template #default="{ row }">
          {{ row.executedAt ? formatDate(row.executedAt) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'test:edit'" link type="success" @click="handleExecute(row as TestCase)">执行</el-button>
          <el-button v-permission="'test:edit'" link type="primary" @click="handleEdit(row as TestCase)">编辑</el-button>
          <el-button v-permission="'test:create'" link type="warning" @click="handleCreateChild(row as TestCase)">添加子用例</el-button>
          <el-button v-permission="'test:delete'" link type="danger" @click="handleDelete(row as TestCase)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="90px">
        <el-form-item label="用例标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入用例标题" />
        </el-form-item>
        <el-form-item label="所属计划">
          <el-select v-model="formData.planId" placeholder="选择测试计划" clearable style="width: 100%">
            <el-option v-for="plan in planOptions" :key="plan.id" :label="plan.name" :value="plan.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="前置条件">
          <el-input v-model="formData.precondition" type="textarea" :rows="2" placeholder="请输入前置条件" />
        </el-form-item>
        <el-form-item label="测试步骤">
          <el-input v-model="formData.steps" type="textarea" :rows="4" placeholder="请输入测试步骤" />
        </el-form-item>
        <el-form-item label="预期结果">
          <el-input v-model="formData.expectedResult" type="textarea" :rows="2" placeholder="请输入预期结果" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="formData.priority" placeholder="请选择优先级">
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

    <!-- 执行对话框 -->
    <el-dialog v-model="executeDialogVisible" title="执行测试用例" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用例标题">
          <span>{{ currentCase?.title }}</span>
        </el-form-item>
        <el-form-item label="执行结果">
          <el-select v-model="executeResult" placeholder="选择执行结果" style="width: 100%">
            <el-option label="通过" value="pass" />
            <el-option label="失败" value="fail" />
            <el-option label="阻塞" value="block" />
            <el-option label="跳过" value="skip" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExecuteSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getTestCaseList,
  createTestCase,
  updateTestCase,
  deleteTestCase,
  executeTestCase,
  getTestPlanList,
} from '@/api/test'
import { formatDate } from '@/utils'
import type { TestCase, TestCaseCreateRequest, CasePriority, CaseResult, TestPlan } from '@/types/test'

const props = defineProps<{ projectId: number }>()
const route = useRoute()

const loading = ref(false)
const caseTree = ref<TestCase[]>([])
const searchKeyword = ref('')
const searchPriority = ref('')
const searchResult = ref('')

// 计划选项
const planOptions = ref<TestPlan[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新建用例')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const parentId = ref<number | null>(null)
const formData = reactive<TestCaseCreateRequest>({
  planId: undefined,
  moduleId: undefined,
  title: '',
  precondition: '',
  steps: '',
  expectedResult: '',
  priority: 'medium',
})
const formRules: FormRules = {
  title: [{ required: true, message: '请输入用例标题', trigger: 'blur' }],
}

// 执行对话框
const executeDialogVisible = ref(false)
const currentCase = ref<TestCase | null>(null)
const executeResult = ref<CaseResult>('pass')

// 映射
const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const resultMap: Record<string, string> = { pass: '通过', fail: '失败', block: '阻塞', skip: '跳过', pending: '未执行' }
const resultTypeMap: Record<string, 'success' | 'danger' | 'warning' | 'info'> = {
  pass: 'success', fail: 'danger', block: 'warning', skip: 'info', pending: 'info',
}

function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function resultLabel(r: string) { return resultMap[r] ?? r }
function resultType(r: string): 'success' | 'danger' | 'warning' | 'info' { return resultTypeMap[r] ?? 'info' }

async function loadPlanOptions() {
  try {
    const data = await getTestPlanList(props.projectId, { page: 1, size: 100 })
    planOptions.value = data.records
  } catch { /* handled */ }
}

async function loadData() {
  loading.value = true
  try {
    const planId = route.query.planId ? Number(route.query.planId) : undefined
    const data = await getTestCaseList(props.projectId, {
      planId,
      keyword: searchKeyword.value || undefined,
      priority: searchPriority.value || undefined,
      result: searchResult.value || undefined,
    })
    caseTree.value = data
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  parentId.value = null
  dialogTitle.value = '新建用例'
  const planId = route.query.planId ? Number(route.query.planId) : undefined
  Object.assign(formData, {
    planId,
    moduleId: undefined,
    title: '',
    precondition: '',
    steps: '',
    expectedResult: '',
    priority: 'medium',
  })
  dialogVisible.value = true
}

function handleCreateChild(parent: TestCase) {
  isEdit.value = false
  editingId.value = null
  parentId.value = parent.id
  dialogTitle.value = '添加子用例'
  Object.assign(formData, {
    planId: parent.planId,
    moduleId: parent.moduleId,
    title: '',
    precondition: '',
    steps: '',
    expectedResult: '',
    priority: 'medium',
  })
  dialogVisible.value = true
}

function handleEdit(row: TestCase) {
  isEdit.value = true
  editingId.value = row.id
  parentId.value = null
  dialogTitle.value = '编辑用例'
  Object.assign(formData, {
    planId: row.planId,
    moduleId: row.moduleId,
    title: row.title,
    precondition: row.precondition,
    steps: row.steps,
    expectedResult: row.expectedResult,
    priority: row.priority,
  })
  dialogVisible.value = true
}

function handleExecute(row: TestCase) {
  currentCase.value = row
  executeResult.value = row.result === 'pending' ? 'pass' : row.result
  executeDialogVisible.value = true
}

async function handleExecuteSubmit() {
  if (!currentCase.value) return
  try {
    await executeTestCase(currentCase.value.id, executeResult.value)
    ElMessage.success('执行结果已记录')
    executeDialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

async function handleDelete(row: TestCase) {
  try {
    await ElMessageBox.confirm(`确定删除用例「${row.title}」？`, '提示', { type: 'warning' })
    await deleteTestCase(row.id)
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
      await updateTestCase(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createTestCase(props.projectId, formData)
      ElMessage.success('创建成功')
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

.case-title {
  color: #303133;
}
</style>
