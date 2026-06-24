<template>
  <div class="delivery-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索交付物名称" prefix-icon="Search" clearable style="width: 220px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="草稿" value="draft" />
          <el-option label="已提交" value="submitted" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
        <el-select v-model="queryParams.type" placeholder="类型" clearable style="width: 120px" @change="handleSearch">
          <el-option label="文档" value="document" />
          <el-option label="代码" value="code" />
          <el-option label="测试报告" value="test_report" />
          <el-option label="其他" value="other" />
        </el-select>
      </div>
      <el-button v-permission="'delivery:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建交付物
      </el-button>
    </div>

    <el-table v-loading="loading" :data="deliveryList" stripe>
      <el-table-column prop="name" label="交付物名称" min-width="200" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ typeLabel(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="version" label="版本" width="70" align="center" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="milestoneName" label="里程碑" width="140" />
      <el-table-column prop="submittedByName" label="提交人" width="100" />
      <el-table-column prop="createdAt" label="创建时间" width="170" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'delivery:view'" link type="primary" @click="handleViewDetail(row as Delivery)">详情</el-button>
          <el-button v-if="row.status === 'draft'" v-permission="'delivery:edit'" link type="success" @click="handleSubmitForReview(row as Delivery)">提交审核</el-button>
          <el-button v-if="row.status === 'submitted'" v-permission="'delivery:manage'" link type="warning" @click="handleReview(row as Delivery)">审核</el-button>
          <el-button v-permission="'delivery:edit'" link type="info" @click="handleNewVersion(row as Delivery)">新版本</el-button>
          <el-button v-permission="'delivery:delete'" link type="danger" @click="handleDelete(row as Delivery)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="交付物名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="formData.type" placeholder="选择类型">
            <el-option label="文档" value="document" />
            <el-option label="代码" value="code" />
            <el-option label="测试报告" value="test_report" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="交付物说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="审核交付物" width="420px">
      <el-form label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewData.status">
            <el-radio value="approved">通过</el-radio>
            <el-radio value="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewData.reviewComment" type="textarea" :rows="3" placeholder="审核意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReviewSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 交付物详情抽屉 -->
    <DeliveryDetailDrawer v-model="detailDrawerVisible" :delivery-id="detailDeliveryId" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getDeliveryList, createDelivery, deleteDelivery, submitDelivery, reviewDelivery } from '@/api/delivery'
import type { Delivery, DeliveryCreateRequest, DeliveryReviewRequest } from '@/types/delivery'
import { DELIVERY_TYPE_LABEL, DELIVERY_STATUS_LABEL, DELIVERY_STATUS_TYPE, labelFrom, tagType } from '@/constants'
import DeliveryDetailDrawer from './components/DeliveryDetailDrawer.vue'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const deliveryList = ref<Delivery[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '', type: '' })

// 对话框
const dialogVisible = ref(false)

// 详情抽屉
const detailDrawerVisible = ref(false)
const detailDeliveryId = ref<number | null>(null)
const dialogTitle = ref('新建交付物')
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const formData = reactive<DeliveryCreateRequest>({ name: '', type: 'document', description: '' })
const formRules: FormRules = { name: [{ required: true, message: '请输入交付物名称', trigger: 'blur' }] }

// 审核
const reviewDialogVisible = ref(false)
const reviewingId = ref<number | null>(null)
const reviewData = reactive<DeliveryReviewRequest>({ status: 'approved', reviewComment: '' })

function typeLabel(t: string) { return labelFrom(DELIVERY_TYPE_LABEL, t) }
function statusLabel(s: string) { return labelFrom(DELIVERY_STATUS_LABEL, s) }
function statusType(s: string) { return tagType(DELIVERY_STATUS_TYPE, s) }

async function loadData() {
  loading.value = true
  try {
    const data = await getDeliveryList(props.projectId, queryParams)
    deliveryList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  dialogTitle.value = '新建交付物'
  Object.assign(formData, { name: '', type: 'document', description: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    await createDelivery(props.projectId, formData)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handleSubmitForReview(row: Delivery) {
  try {
    await ElMessageBox.confirm(`确定提交「${row.name}」审核？`, '提示', { type: 'info' })
    await submitDelivery(row.id)
    ElMessage.success('提交成功')
    loadData()
  } catch { /* cancelled */ }
}

function handleReview(row: Delivery) {
  reviewingId.value = row.id
  reviewData.status = 'approved'
  reviewData.reviewComment = ''
  reviewDialogVisible.value = true
}

async function handleReviewSubmit() {
  if (!reviewingId.value) return
  try {
    await reviewDelivery(reviewingId.value, reviewData)
    ElMessage.success('审核完成')
    reviewDialogVisible.value = false
    loadData()
  } catch { /* handled */ }
}

function handleViewDetail(row: Delivery) {
  detailDeliveryId.value = row.id
  detailDrawerVisible.value = true
}

function handleNewVersion(row: Delivery) {
  dialogTitle.value = `创建新版本（当前 v${row.version}）`
  Object.assign(formData, { name: row.name, type: row.type, description: row.description })
  dialogVisible.value = true
}

async function handleDelete(row: Delivery) {
  try {
    await ElMessageBox.confirm(`确定删除「${row.name}」？`, '提示', { type: 'warning' })
    await deleteDelivery(row.id)
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
