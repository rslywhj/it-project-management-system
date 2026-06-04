<template>
  <div class="test-plan-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索计划名称"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="草稿" value="draft" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="completed" />
          <el-option label="已归档" value="archived" />
        </el-select>
      </div>
      <el-button v-permission="'test:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建计划
      </el-button>
    </div>

    <el-table v-loading="loading" :data="planList" stripe>
      <el-table-column prop="name" label="计划名称" min-width="200">
        <template #default="{ row }">
          <span class="plan-name" @click="handleViewCases(row as TestPlan)">{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="caseCount" label="用例数" width="80" align="center" />
      <el-table-column label="通过率" width="120">
        <template #default="{ row }">
          <el-progress
            :percentage="row.caseCount > 0 ? Number(row.passRate) : 0"
            :stroke-width="10"
            :status="Number(row.passRate) >= 80 ? 'success' : (Number(row.passRate) >= 60 ? 'warning' : 'exception')"
          />
        </template>
      </el-table-column>
      <el-table-column prop="startDate" label="开始日期" width="120">
        <template #default="{ row }">
          {{ row.startDate ? formatDate(row.startDate) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="endDate" label="结束日期" width="120">
        <template #default="{ row }">
          {{ row.endDate ? formatDate(row.endDate) : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="createdByName" label="创建人" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="170">
        <template #default="{ row }">
          {{ formatDate(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'test:edit'" link type="primary" @click="handleEdit(row as TestPlan)">编辑</el-button>
          <el-button v-permission="'test:view'" link type="primary" @click="handleViewCases(row as TestPlan)">用例</el-button>
          <el-button v-permission="'test:delete'" link type="danger" @click="handleDelete(row as TestPlan)">删除</el-button>
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

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑测试计划' : '新建测试计划'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="计划名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入计划描述" />
        </el-form-item>
        <el-form-item label="起止日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getTestPlanList, createTestPlan, updateTestPlan, deleteTestPlan } from '@/api/test'
import { formatDate } from '@/utils'
import type { TestPlan, TestPlanCreateRequest, TestPlanStatus } from '@/types/test'

const props = defineProps<{ projectId: number }>()
const router = useRouter()

const loading = ref(false)
const planList = ref<TestPlan[]>([])
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
const dateRange = ref<[string, string] | null>(null)
const formData = reactive<TestPlanCreateRequest>({
  name: '',
  description: '',
  startDate: '',
  endDate: '',
})
const formRules: FormRules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
}

// 状态映射
const statusLabelMap: Record<string, string> = {
  draft: '草稿', in_progress: '进行中', completed: '已完成', archived: '已归档',
}
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  draft: 'info', in_progress: 'warning', completed: 'success', archived: 'danger',
}

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

async function loadData() {
  loading.value = true
  try {
    const data = await getTestPlanList(props.projectId, queryParams)
    planList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
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
  Object.assign(formData, {
    name: row.name,
    description: row.description,
    startDate: row.startDate,
    endDate: row.endDate,
  })
  if (row.startDate && row.endDate) {
    dateRange.value = [row.startDate, row.endDate]
  } else {
    dateRange.value = null
  }
  dialogVisible.value = true
}

function handleViewCases(row: TestPlan) {
  router.push({ path: '/test/cases', query: { planId: row.id } })
}

async function handleDelete(row: TestPlan) {
  try {
    await ElMessageBox.confirm(`确定删除测试计划「${row.name}」？`, '提示', { type: 'warning' })
    await deleteTestPlan(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (dateRange.value) {
      formData.startDate = dateRange.value[0]
      formData.endDate = dateRange.value[1]
    } else {
      formData.startDate = ''
      formData.endDate = ''
    }
    if (isEdit.value && editingId.value) {
      await updateTestPlan(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createTestPlan(props.projectId, formData)
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

.plan-name {
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
