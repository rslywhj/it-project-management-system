<template>
  <div class="milestone-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="待开始" value="pending" />
          <el-option label="进行中" value="in_progress" />
          <el-option label="已完成" value="completed" />
          <el-option label="已延期" value="delayed" />
        </el-select>
      </div>
      <el-button v-permission="'milestone:create'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建里程碑
      </el-button>
    </div>

    <!-- 里程碑时间线 -->
    <el-timeline v-if="milestoneList.length > 0">
      <el-timeline-item
        v-for="item in milestoneList"
        :key="item.id"
        :timestamp="item.plannedDate"
        :type="timelineType(item.status)"
        :hollow="item.status === 'pending'"
        placement="top"
      >
        <el-card shadow="hover" class="milestone-card">
          <div class="milestone-header">
            <div class="milestone-info">
              <h4>{{ item.name }}</h4>
              <el-tag :type="statusTagType(item.status)" size="small">
                {{ statusLabel(item.status) }}
              </el-tag>
            </div>
            <div class="milestone-actions">
              <el-button v-permission="'milestone:edit'" link type="primary" @click="handleEdit(item)">编辑</el-button>
              <el-button v-permission="'milestone:delete'" link type="danger" @click="handleDelete(item)">删除</el-button>
            </div>
          </div>
          <p v-if="item.description" class="milestone-desc">{{ item.description }}</p>
          <div class="milestone-meta">
            <span v-if="item.actualDate">实际完成：{{ item.actualDate }}</span>
            <span v-if="item.status === 'delayed'" class="delayed">已延期</span>
          </div>
        </el-card>
      </el-timeline-item>
    </el-timeline>

    <el-empty v-else description="暂无里程碑" />

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>

    <!-- 新建/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑里程碑' : '新建里程碑'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入里程碑名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入里程碑描述" />
        </el-form-item>
        <el-form-item label="计划日期" prop="plannedDate">
          <el-date-picker
            v-model="formData.plannedDate"
            type="date"
            placeholder="选择计划日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="isEdit" label="实际完成日期">
          <el-date-picker
            v-model="formData.actualDate"
            type="date"
            placeholder="选择实际完成日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态">
          <el-select v-model="formData.status" placeholder="选择状态">
            <el-option label="待开始" value="pending" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已完成" value="completed" />
            <el-option label="已延期" value="delayed" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序序号">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
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
import {
  getMilestoneList,
  createMilestone,
  updateMilestone,
  deleteMilestone,
} from '@/api/milestone'
import type { Milestone, MilestoneCreateRequest, MilestoneStatus, MilestoneUpdateRequest } from '@/types/milestone'

const props = defineProps<{ projectId: number }>()

const loading = ref(false)
const milestoneList = ref<Milestone[]>([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  pageSize: 10,
  status: '',
})

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<MilestoneCreateRequest & { actualDate?: string; status?: MilestoneStatus }>({
  name: '',
  description: '',
  plannedDate: '',
  sortOrder: 0,
  actualDate: '',
  status: undefined,
})
const formRules: FormRules = {
  name: [{ required: true, message: '请输入里程碑名称', trigger: 'blur' }],
  plannedDate: [{ required: true, message: '请选择计划日期', trigger: 'change' }],
}

// 状态映射
const statusLabelMap: Record<string, string> = {
  pending: '待开始',
  in_progress: '进行中',
  completed: '已完成',
  delayed: '已延期',
}
const statusTypeMap: Record<string, string> = {
  pending: 'info',
  in_progress: 'warning',
  completed: 'success',
  delayed: 'danger',
}

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusTagType(s: string) { return (statusTypeMap[s] ?? '') as '' | 'success' | 'warning' | 'info' | 'danger' }
function timelineType(s: string) {
  const map: Record<string, string> = { pending: 'info', in_progress: 'warning', completed: 'success', delayed: 'danger' }
  return (map[s] ?? 'info') as '' | 'success' | 'warning' | 'info' | 'danger'
}

async function loadData() {
  loading.value = true
  try {
    const { data } = await getMilestoneList(props.projectId, queryParams)
    milestoneList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { name: '', description: '', plannedDate: '', sortOrder: 0, actualDate: '', status: undefined })
  dialogVisible.value = true
}

function handleEdit(row: Milestone) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    name: row.name,
    description: row.description,
    plannedDate: row.plannedDate,
    sortOrder: row.sortOrder,
    actualDate: row.actualDate,
    status: row.status,
  })
  dialogVisible.value = true
}

async function handleDelete(row: Milestone) {
  try {
    await ElMessageBox.confirm(`确定删除里程碑「${row.name}」？`, '提示', { type: 'warning' })
    await deleteMilestone(row.id)
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
      const updateData: MilestoneUpdateRequest = {
        name: formData.name,
        description: formData.description,
        plannedDate: formData.plannedDate,
        sortOrder: formData.sortOrder,
        actualDate: formData.actualDate,
        status: formData.status,
      }
      await updateMilestone(editingId.value, updateData)
      ElMessage.success('更新成功')
    } else {
      await createMilestone(props.projectId, formData)
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
  margin-bottom: 16px;

  .toolbar-left {
    display: flex;
    gap: 8px;
  }
}

.milestone-card {
  .milestone-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .milestone-info {
    display: flex;
    align-items: center;
    gap: 8px;

    h4 {
      margin: 0;
      font-size: 16px;
    }
  }

  .milestone-desc {
    margin: 8px 0 0;
    color: #606266;
    font-size: 14px;
  }

  .milestone-meta {
    margin-top: 8px;
    color: #909399;
    font-size: 13px;
    display: flex;
    gap: 16px;

    .delayed {
      color: #f56c6c;
      font-weight: 600;
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
