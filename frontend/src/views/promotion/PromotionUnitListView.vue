<template>
  <div class="promotion-unit-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索单位名称或编码"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="待启动" value="pending" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="completed" />
          <el-option label="已暂停" value="suspended" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button v-permission="'promotion:create'" type="primary" size="small" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建推广单元
        </el-button>
        <el-button v-permission="'promotion:create'" type="success" size="small" @click="handleBatchCreate">
          <el-icon><DocumentAdd /></el-icon>批量创建
        </el-button>
      </div>
    </div>

    <el-table v-loading="loading" :data="unitList" stripe>
      <el-table-column prop="orgName" label="成员单位" min-width="180" />
      <el-table-column prop="orgCode" label="编码" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="进度" width="160">
        <template #default="{ row }">
          <el-progress :percentage="Number(row.completionRate)" :stroke-width="8" :status="progressStatus(row.status)" />
        </template>
      </el-table-column>
      <el-table-column prop="currentStageName" label="当前阶段" width="140" />
      <el-table-column prop="responsibleUserName" label="负责人" width="100" />
      <el-table-column prop="expectedEndDate" label="计划完成" width="110" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'promotion:view'" link type="primary" @click="handleViewProgress(row as PromotionUnit)">进度</el-button>
          <el-button v-permission="'promotion:view'" link type="success" @click="handleViewRequirements(row as PromotionUnit)">需求</el-button>
          <el-button v-permission="'promotion:edit'" link type="warning" @click="handleEdit(row as PromotionUnit)">编辑</el-button>
          <el-button v-permission="'promotion:delete'" link type="danger" @click="handleDelete(row as PromotionUnit)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑推广单元' : '新建推广单元'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="成员单位" prop="orgName">
          <el-input v-model="formData.orgName" placeholder="请输入成员单位名称" />
        </el-form-item>
        <el-form-item label="单位编码" prop="orgCode">
          <el-input v-model="formData.orgCode" placeholder="如 BJ001、SH002" />
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
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 批量创建对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量创建推广单元" width="600px" destroy-on-close>
      <el-alert title="基于已有成员单位列表批量创建推广单元，每行一个单位" type="info" :closable="false" style="margin-bottom: 16px" />
      <el-form label-width="100px">
        <el-form-item label="单位列表">
          <el-input
            v-model="batchText"
            type="textarea"
            :rows="8"
            placeholder="每行格式：单位编码,单位名称&#10;例如：&#10;BJ001,北京分公司&#10;SH002,上海分公司&#10;GZ003,广州分公司"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 进度详情抽屉 -->
    <el-drawer v-model="progressDrawerVisible" :title="`${currentUnit?.orgName} - 推广进度`" size="500px">
      <ProgressDetail v-if="currentUnit" :unit="currentUnit" />
    </el-drawer>

    <!-- 差异化需求抽屉 -->
    <el-drawer v-model="requirementDrawerVisible" :title="`${currentUnit?.orgName} - 差异化需求`" size="600px">
      <RequirementDetail v-if="currentUnit" :unit-id="currentUnit.id" />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  getPromotionUnitList,
  createPromotionUnit,
  updatePromotionUnit,
  deletePromotionUnit,
  batchCreatePromotionUnits,
} from '@/api/promotion'
import type { PromotionUnit, PromotionUnitCreateRequest } from '@/types/promotion'
import ProgressDetail from './components/ProgressDetail.vue'
import RequirementDetail from './components/RequirementDetail.vue'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const unitList = ref<PromotionUnit[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '' })

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const dateRange = ref<[string, string] | null>(null)
const formData = reactive<PromotionUnitCreateRequest>({
  orgName: '',
  orgCode: '',
  expectedStartDate: '',
  expectedEndDate: '',
  remark: '',
})
const formRules: FormRules = {
  orgName: [{ required: true, message: '请输入成员单位名称', trigger: 'blur' }],
  orgCode: [{ required: true, message: '请输入单位编码', trigger: 'blur' }],
}

// 批量创建
const batchDialogVisible = ref(false)
const batchLoading = ref(false)
const batchText = ref('')

// 抽屉
const progressDrawerVisible = ref(false)
const requirementDrawerVisible = ref(false)
const currentUnit = ref<PromotionUnit | null>(null)

// 映射
const statusLabelMap: Record<string, string> = { pending: '待启动', in_progress: '进行中', completed: '已完成', suspended: '已暂停' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  pending: 'info', in_progress: 'warning', completed: 'success', suspended: 'danger',
}

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }
function progressStatus(s: string) { return s === 'completed' ? 'success' : undefined }

async function loadData() {
  loading.value = true
  try {
    const data = await getPromotionUnitList(props.projectId, queryParams)
    unitList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { orgName: '', orgCode: '', expectedStartDate: '', expectedEndDate: '', remark: '' })
  dateRange.value = null
  dialogVisible.value = true
}

function handleEdit(row: PromotionUnit) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    orgName: row.orgName,
    orgCode: row.orgCode,
    expectedStartDate: row.expectedStartDate,
    expectedEndDate: row.expectedEndDate,
    remark: row.remark,
  })
  if (row.expectedStartDate && row.expectedEndDate) dateRange.value = [row.expectedStartDate, row.expectedEndDate]
  dialogVisible.value = true
}

function handleBatchCreate() {
  batchText.value = ''
  batchDialogVisible.value = true
}

function handleDateChange(val: [string, string] | null) {
  formData.expectedStartDate = val?.[0] ?? ''
  formData.expectedEndDate = val?.[1] ?? ''
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updatePromotionUnit(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createPromotionUnit(props.projectId, formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    submitLoading.value = false
  }
}

async function handleBatchSubmit() {
  const lines = batchText.value.trim().split('\n').filter((l) => l.trim())
  if (lines.length === 0) {
    ElMessage.warning('请输入至少一个单位')
    return
  }
  const units: PromotionUnitCreateRequest[] = lines.map((line) => {
    const [code, name] = line.split(',').map((s) => s.trim())
    return { orgName: name || code, orgCode: code }
  })
  batchLoading.value = true
  try {
    await batchCreatePromotionUnits(props.projectId, units)
    ElMessage.success(`成功创建 ${units.length} 个推广单元`)
    batchDialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally {
    batchLoading.value = false
  }
}

async function handleDelete(row: any) {
  try {
    await ElMessageBox.confirm(`确定删除推广单元「${row.orgName}」？`, '提示', { type: 'warning' })
    await deletePromotionUnit(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

function handleViewProgress(row: any) {
  currentUnit.value = row as PromotionUnit
  progressDrawerVisible.value = true
}

function handleViewRequirements(row: any) {
  currentUnit.value = row as PromotionUnit
  requirementDrawerVisible.value = true
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .toolbar-left { display: flex; gap: 8px; }
  .toolbar-right { display: flex; gap: 8px; }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
