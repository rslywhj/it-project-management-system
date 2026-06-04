<template>
  <div class="test-case-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索用例标题" prefix-icon="Search" clearable style="width: 220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.priority" placeholder="优先级" clearable style="width: 100px" @change="handleSearch">
          <el-option label="紧急" value="critical" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
        </el-select>
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 100px" @change="handleSearch">
          <el-option label="草稿" value="draft" />
          <el-option label="就绪" value="ready" />
          <el-option label="阻塞" value="blocked" />
        </el-select>
      </div>
      <el-button v-permission="'test:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建用例
      </el-button>
    </div>

    <el-table v-loading="loading" :data="caseList" stripe>
      <el-table-column prop="title" label="用例标题" min-width="240" />
      <el-table-column prop="moduleId" label="模块" width="100" />
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
      <el-table-column prop="assignedToName" label="执行人" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'test:edit'" link type="primary" @click="handleEdit(row as TestCase)">编辑</el-button>
          <el-button v-permission="'test:delete'" link type="danger" @click="handleDelete(row as TestCase)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用例' : '新建用例'" width="580px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="用例标题" />
        </el-form-item>
        <el-form-item label="模块">
          <el-input v-model="formData.moduleId" placeholder="所属模块" />
        </el-form-item>
        <el-form-item label="前置条件">
          <el-input v-model="formData.precondition" type="textarea" :rows="2" placeholder="前置条件" />
        </el-form-item>
        <el-form-item label="测试步骤">
          <el-input v-model="formData.steps" type="textarea" :rows="4" placeholder="1. 步骤一&#10;2. 步骤二&#10;3. 步骤三" />
        </el-form-item>
        <el-form-item label="预期结果">
          <el-input v-model="formData.expectedResult" type="textarea" :rows="2" placeholder="预期结果" />
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
import { getTestCaseList, createTestCase, updateTestCase, deleteTestCase } from '@/api/test'
import type { TestCase, TestCaseCreateRequest } from '@/types/test'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const caseList = ref<TestCase[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', priority: '', status: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<TestCaseCreateRequest>({
  title: '', moduleId: '', precondition: '', steps: '', expectedResult: '', priority: 'medium',
})
const formRules: FormRules = { title: [{ required: true, message: '请输入用例标题', trigger: 'blur' }] }

const priorityMap: Record<string, string> = { critical: '紧急', high: '高', medium: '中', low: '低' }
const priorityTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const statusLabelMap: Record<string, string> = { draft: '草稿', ready: '就绪', blocked: '阻塞' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = { draft: 'info', ready: 'success', blocked: 'danger' }

function priorityLabel(p: string) { return priorityMap[p] ?? p }
function priorityType(p: string): 'danger' | 'warning' | 'info' { return priorityTypeMap[p] ?? 'info' }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadData() {
  loading.value = true
  try {
    const data = await getTestCaseList(props.projectId, queryParams)
    caseList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', moduleId: '', precondition: '', steps: '', expectedResult: '', priority: 'medium' })
  dialogVisible.value = true
}

function handleEdit(row: TestCase) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    title: row.title, moduleId: row.moduleId, precondition: row.precondition,
    steps: row.steps, expectedResult: row.expectedResult, priority: row.priority,
  })
  dialogVisible.value = true
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
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handleDelete(row: TestCase) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.title}」？`, '提示', { type: 'warning' })
    await deleteTestCase(row.id)
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
