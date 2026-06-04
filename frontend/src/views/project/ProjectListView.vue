<template>
  <div class="project-list">
    <!-- 页面头部 -->
    <el-card shadow="never" class="filter-card">
      <div class="filter-row">
        <div class="filter-left">
          <el-input
            v-model="queryParams.keyword"
            placeholder="搜索项目名称或编码"
            prefix-icon="Search"
            clearable
            style="width: 260px"
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
          <el-select
            v-model="queryParams.status"
            placeholder="项目状态"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option label="规划中" value="planning" />
            <el-option label="进行中" value="active" />
            <el-option label="已暂停" value="suspended" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </div>
        <el-button v-permission="'project:create'" type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建项目
        </el-button>
      </div>
    </el-card>

    <!-- 项目列表 -->
    <el-card shadow="never" style="margin-top: 12px">
      <el-table
        v-loading="loading"
        :data="projectList"
        stripe
        @row-click="handleRowClick"
      >
        <el-table-column prop="projectCode" label="项目编码" width="140" />
        <el-table-column prop="name" label="项目名称" min-width="200">
          <template #default="{ row }">
            <span class="project-name">{{ row.name }}</span>
            <el-tag v-if="row.promotionEnabled" size="small" type="warning" style="margin-left: 8px">
              推广
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="memberCount" label="成员" width="80" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'project:edit'" link type="primary" @click.stop="handleEdit(row)">
              编辑
            </el-button>
            <el-button v-permission="'project:delete'" link type="danger" @click.stop="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSearch"
          @current-change="handleSearch"
        />
      </div>
    </el-card>

    <!-- 新建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑项目' : '新建项目'"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="项目编码" prop="projectCode">
          <el-input v-model="formData.projectCode" :disabled="isEdit" placeholder="如 PRJ-2026-001" />
        </el-form-item>
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目类型" prop="type">
          <el-select v-model="formData.type" placeholder="请选择项目类型">
            <el-option label="研发项目" value="development" />
            <el-option label="推广项目" value="promotion" />
            <el-option label="运维项目" value="maintenance" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        <el-form-item label="启用推广">
          <el-switch v-model="formData.promotionEnabled" />
        </el-form-item>
        <el-form-item label="计划时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
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
import { getProjectList, createProject, updateProject, deleteProject } from '@/api/project'
import { formatDate } from '@/utils'
import type { Project, ProjectCreateRequest } from '@/types/project'
import type { PageParams } from '@/types/api'

const router = useRouter()

// 列表相关
const loading = ref(false)
const projectList = ref<Project[]>([])
const total = ref(0)
const queryParams = reactive<PageParams & { keyword?: string; status?: string }>({
  page: 1,
  pageSize: 10,
  keyword: '',
  status: '',
})

// 对话框相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const dateRange = ref<[string, string] | null>(null)

const formData = reactive<ProjectCreateRequest & { status?: string }>({
  projectCode: '',
  name: '',
  description: '',
  type: '',
  promotionEnabled: false,
  startDate: '',
  endDate: '',
})

const formRules: FormRules = {
  projectCode: [{ required: true, message: '请输入项目编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
}

// 状态映射
const statusMap: Record<string, { label: string; type: string }> = {
  planning: { label: '规划中', type: 'info' },
  active: { label: '进行中', type: '' },
  suspended: { label: '已暂停', type: 'warning' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'danger' },
}

function statusLabel(status: string) {
  return statusMap[status]?.label ?? status
}

function statusTagType(status: string) {
  return (statusMap[status]?.type ?? '') as '' | 'success' | 'warning' | 'info' | 'danger'
}

// 加载数据
async function loadProjects() {
  loading.value = true
  try {
    const { data } = await getProjectList(queryParams)
    projectList.value = data.records
    total.value = data.total
  } catch {
    // API 错误已由拦截器处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadProjects()
}

function handleRowClick(row: Project) {
  router.push(`/project/${row.id}`)
}

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: Project) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    projectCode: row.projectCode,
    name: row.name,
    description: row.description,
    type: row.type,
    promotionEnabled: row.promotionEnabled,
    startDate: row.startDate,
    endDate: row.endDate,
  })
  if (row.startDate && row.endDate) {
    dateRange.value = [row.startDate, row.endDate]
  }
  dialogVisible.value = true
}

async function handleDelete(row: Project) {
  try {
    await ElMessageBox.confirm(`确定删除项目「${row.name}」？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await deleteProject(row.id)
    ElMessage.success('删除成功')
    loadProjects()
  } catch {
    // 用户取消
  }
}

function handleDateChange(val: [string, string] | null) {
  if (val) {
    formData.startDate = val[0]
    formData.endDate = val[1]
  } else {
    formData.startDate = ''
    formData.endDate = ''
  }
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateProject(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createProject(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadProjects()
  } catch {
    // API 错误已由拦截器处理
  } finally {
    submitLoading.value = false
  }
}

function resetForm() {
  Object.assign(formData, {
    projectCode: '',
    name: '',
    description: '',
    type: '',
    promotionEnabled: false,
    startDate: '',
    endDate: '',
  })
  dateRange.value = null
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped lang="scss">
.filter-card {
  .filter-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .filter-left {
    display: flex;
    gap: 12px;
  }
}

.project-name {
  color: #409eff;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
