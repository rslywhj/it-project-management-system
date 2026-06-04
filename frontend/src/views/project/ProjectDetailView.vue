<template>
  <div class="project-detail" v-loading="loading">
    <!-- 项目头部信息 -->
    <el-card shadow="never" class="header-card">
      <div class="header">
        <div class="header-left">
          <div class="title-row">
            <h2>{{ project?.name }}</h2>
            <el-tag :type="statusTagType(project?.status ?? '')" size="small">
              {{ statusLabel(project?.status ?? '') }}
            </el-tag>
            <el-tag v-if="project?.promotionEnabled" size="small" type="warning">推广</el-tag>
          </div>
          <div class="meta">
            <span>编码：{{ project?.projectCode }}</span>
            <span>类型：{{ project?.type }}</span>
            <span v-if="project?.startDate">周期：{{ project.startDate }} ~ {{ project.endDate }}</span>
            <span>成员：{{ project?.memberCount ?? 0 }} 人</span>
          </div>
          <p v-if="project?.description" class="desc">{{ project.description }}</p>
        </div>
        <div class="header-right">
          <el-button v-permission="'project:edit'" @click="handleEdit">编辑</el-button>
          <el-button v-permission="'requirement:create'" type="primary" @click="activeTab = 'requirement'">
            需求管理
          </el-button>
          <el-button v-permission="'task:create'" type="success" @click="activeTab = 'task'">
            任务管理
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- Tab 切换 -->
    <el-card shadow="never" style="margin-top: 12px">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="需求管理" name="requirement">
          <RequirementList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="任务管理" name="task">
          <TaskList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="里程碑" name="milestone">
          <MilestoneList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="团队成员" name="member">
          <MemberList :project-id="projectId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 编辑项目对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑项目" width="560px" destroy-on-close>
      <el-form ref="editFormRef" :model="editFormData" :rules="editFormRules" label-width="100px">
        <el-form-item label="项目名称" prop="name">
          <el-input v-model="editFormData.name" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目描述">
          <el-input v-model="editFormData.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        <el-form-item label="项目状态">
          <el-select v-model="editFormData.status" placeholder="请选择状态">
            <el-option label="规划中" value="planning" />
            <el-option label="进行中" value="in_progress" />
            <el-option label="已暂停" value="on_hold" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划时间">
          <el-date-picker
            v-model="editDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleEditDateChange"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleEditSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { getProjectDetail, updateProject } from '@/api/project'
import type { Project, ProjectStatus } from '@/types/project'
import RequirementList from '@/views/requirement/RequirementListView.vue'
import TaskList from '@/views/task/TaskListView.vue'
import MilestoneList from '@/views/milestone/MilestoneListView.vue'
import MemberList from './components/MemberList.vue'

const route = useRoute()
const projectId = Number(route.params.id)

const loading = ref(false)
const project = ref<Project | null>(null)
const activeTab = ref('requirement')

// 编辑对话框
const editDialogVisible = ref(false)
const editLoading = ref(false)
const editFormRef = ref<FormInstance>()
const editDateRange = ref<[string, string] | null>(null)
const editFormData = reactive({
  name: '',
  description: '',
  status: '' as ProjectStatus | '',
  startDate: '',
  endDate: '',
})
const editFormRules: FormRules = {
  name: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
}

const statusMap: Record<string, { label: string; type: 'info' | 'warning' | 'success' | 'danger' }> = {
  planning: { label: '规划中', type: 'info' },
  in_progress: { label: '进行中', type: 'info' },
  on_hold: { label: '已暂停', type: 'warning' },
  closed: { label: '已关闭', type: 'success' },
}

function statusLabel(status: string) {
  return statusMap[status]?.label ?? status
}

function statusTagType(status: string) {
  return (statusMap[status]?.type ?? 'info') as 'primary' | 'success' | 'warning' | 'info' | 'danger'
}

async function loadProject() {
  loading.value = true
  try {
    const data = await getProjectDetail(projectId)
    project.value = data
  } catch {
    // API 错误已由拦截器处理
  } finally {
    loading.value = false
  }
}

function handleEdit() {
  if (!project.value) return
  editFormData.name = project.value.name
  editFormData.description = project.value.description ?? ''
  editFormData.status = project.value.status
  editFormData.startDate = project.value.startDate ?? ''
  editFormData.endDate = project.value.endDate ?? ''
  if (project.value.startDate && project.value.endDate) {
    editDateRange.value = [project.value.startDate, project.value.endDate]
  } else {
    editDateRange.value = null
  }
  editDialogVisible.value = true
}

function handleEditDateChange(val: [string, string] | null) {
  editFormData.startDate = val?.[0] ?? ''
  editFormData.endDate = val?.[1] ?? ''
}

async function handleEditSubmit() {
  const valid = await editFormRef.value?.validate().catch(() => false)
  if (!valid) return
  editLoading.value = true
  try {
    const updated = await updateProject(projectId, {
      name: editFormData.name,
      description: editFormData.description,
      status: editFormData.status as ProjectStatus,
      startDate: editFormData.startDate,
      endDate: editFormData.endDate,
    })
    project.value = updated
    ElMessage.success('更新成功')
    editDialogVisible.value = false
  } catch {
    // 错误已由拦截器处理
  } finally {
    editLoading.value = false
  }
}

onMounted(() => {
  loadProject()
})
</script>

<style scoped lang="scss">
.header-card {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .title-row {
    display: flex;
    align-items: center;
    gap: 8px;

    h2 {
      margin: 0;
      font-size: 20px;
    }
  }

  .meta {
    margin-top: 8px;
    color: #909399;
    font-size: 14px;
    display: flex;
    gap: 16px;
  }

  .desc {
    margin-top: 8px;
    color: #606266;
    font-size: 14px;
  }
}
</style>
