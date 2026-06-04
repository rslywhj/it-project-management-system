<template>
  <div class="risk-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索风险标题" prefix-icon="Search" clearable style="width: 200px" @clear="handleSearch" @keyup.enter="handleSearch" />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="已识别" value="identified" />
          <el-option label="分析中" value="analyzing" />
          <el-option label="缓解中" value="mitigating" />
          <el-option label="已发生" value="occurred" />
          <el-option label="已关闭" value="closed" />
        </el-select>
        <el-select v-model="queryParams.level" placeholder="等级" clearable style="width: 100px" @change="handleSearch">
          <el-option label="严重" value="critical" />
          <el-option label="高" value="high" />
          <el-option label="中" value="medium" />
          <el-option label="低" value="low" />
        </el-select>
      </div>
      <el-button v-permission="'risk:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建风险
      </el-button>
    </div>

    <!-- 风险矩阵概览 -->
    <el-row :gutter="16" style="margin-bottom: 16px">
      <el-col :span="6" v-for="stat in riskStats" :key="stat.level">
        <el-card shadow="hover" class="risk-stat-card" :class="stat.level">
          <div class="stat-value">{{ stat.count }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="riskList" stripe>
      <el-table-column prop="title" label="风险标题" min-width="200" />
      <el-table-column prop="level" label="等级" width="80">
        <template #default="{ row }">
          <el-tag :type="levelType(row.level)" size="small">{{ levelLabel(row.level) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="概率/影响" width="120" align="center">
        <template #default="{ row }">
          <span>{{ row.probability }}/{{ row.impact }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="riskScore" label="风险分" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.riskScore >= 15 ? 'danger' : row.riskScore >= 8 ? 'warning' : 'info'" size="small">{{ row.riskScore }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="ownerName" label="责任人" width="100" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'risk:edit'" link type="primary" @click="handleEdit(row as Risk)">编辑</el-button>
          <el-button v-if="row.status !== 'closed'" v-permission="'risk:manage'" link type="success" @click="handleClose(row as Risk)">关闭</el-button>
          <el-button v-permission="'risk:delete'" link type="danger" @click="handleDelete(row as Risk)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.size" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @size-change="handleSearch" @current-change="handleSearch" />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑风险' : '新建风险'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="风险标题" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="风险描述" />
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="formData.category" placeholder="如：技术风险、进度风险、资源风险" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="等级">
              <el-select v-model="formData.level">
                <el-option label="严重" value="critical" />
                <el-option label="高" value="high" />
                <el-option label="中" value="medium" />
                <el-option label="低" value="low" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="概率">
              <el-input-number v-model="formData.probability" :min="1" :max="5" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="影响">
              <el-input-number v-model="formData.impact" :min="1" :max="5" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="缓解计划">
          <el-input v-model="formData.mitigationPlan" type="textarea" :rows="2" placeholder="缓解措施" />
        </el-form-item>
        <el-form-item label="应急计划">
          <el-input v-model="formData.contingencyPlan" type="textarea" :rows="2" placeholder="应急措施" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getRiskList, createRisk, updateRisk, deleteRisk, closeRisk } from '@/api/risk'
import type { Risk, RiskCreateRequest } from '@/types/risk'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const riskList = ref<Risk[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '', level: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<RiskCreateRequest>({
  title: '', description: '', category: '', level: 'medium', probability: 3, impact: 3, mitigationPlan: '', contingencyPlan: '',
})
const formRules: FormRules = { title: [{ required: true, message: '请输入风险标题', trigger: 'blur' }] }

const levelMap: Record<string, string> = { critical: '严重', high: '高', medium: '中', low: '低' }
const levelTypeMap: Record<string, 'danger' | 'warning' | 'info'> = { critical: 'danger', high: 'warning', medium: 'info', low: 'info' }
const statusLabelMap: Record<string, string> = { identified: '已识别', analyzing: '分析中', mitigating: '缓解中', occurred: '已发生', closed: '已关闭' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success' | 'danger'> = {
  identified: 'info', analyzing: 'warning', mitigating: 'warning', occurred: 'danger', closed: 'success',
}

function levelLabel(l: string) { return levelMap[l] ?? l }
function levelType(l: string): 'danger' | 'warning' | 'info' { return levelTypeMap[l] ?? 'info' }
function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string): 'info' | 'warning' | 'success' | 'danger' { return statusTypeMap[s] ?? 'info' }

const riskStats = computed(() => [
  { level: 'critical', label: '严重风险', count: riskList.value.filter((r) => r.level === 'critical' && r.status !== 'closed').length },
  { level: 'high', label: '高风险', count: riskList.value.filter((r) => r.level === 'high' && r.status !== 'closed').length },
  { level: 'medium', label: '中风险', count: riskList.value.filter((r) => r.level === 'medium' && r.status !== 'closed').length },
  { level: 'low', label: '低风险', count: riskList.value.filter((r) => r.level === 'low' && r.status !== 'closed').length },
])

async function loadData() {
  loading.value = true
  try {
    const data = await getRiskList(props.projectId, queryParams)
    riskList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { title: '', description: '', category: '', level: 'medium', probability: 3, impact: 3, mitigationPlan: '', contingencyPlan: '' })
  dialogVisible.value = true
}

function handleEdit(row: Risk) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    title: row.title, description: row.description, category: row.category,
    level: row.level, probability: row.probability, impact: row.impact,
    mitigationPlan: row.mitigationPlan, contingencyPlan: row.contingencyPlan,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateRisk(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createRisk(props.projectId, formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handleClose(row: Risk) {
  try {
    await ElMessageBox.confirm(`确定关闭风险「${row.title}」？`, '提示', { type: 'warning' })
    await closeRisk(row.id)
    ElMessage.success('风险已关闭')
    loadData()
  } catch { /* cancelled */ }
}

async function handleDelete(row: Risk) {
  try {
    await ElMessageBox.confirm(`确定删除风险「${row.title}」？`, '提示', { type: 'warning' })
    await deleteRisk(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 12px; }
.risk-stat-card {
  text-align: center;
  .stat-value { font-size: 28px; font-weight: 600; }
  .stat-label { margin-top: 4px; color: #909399; font-size: 13px; }
  &.critical .stat-value { color: #f56c6c; }
  &.high .stat-value { color: #e6a23c; }
  &.medium .stat-value { color: #409eff; }
  &.low .stat-value { color: #909399; }
}
</style>
