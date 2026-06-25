<template>
  <div class="dashboard">
    <el-card shadow="never" style="margin-bottom: 16px">
      <div style="display: flex; align-items: center; gap: 12px">
        <span style="font-size: 16px; font-weight: 600">项目看板</span>
        <el-select v-model="selectedProjectId" placeholder="请选择项目" style="width: 240px" @change="loadDashboard">
          <el-option v-for="p in projectList" :key="p.id" :label="p.name" :value="p.id" />
        </el-select>
      </div>
    </el-card>

    <template v-if="selectedProjectId">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-info">
                <span class="label">需求总数</span>
                <span class="value">{{ dashboard.stats.totalRequirements }}</span>
              </div>
              <el-icon :size="40" color="#409eff"><Document /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-info">
                <span class="label">任务总数</span>
                <span class="value">{{ dashboard.stats.totalTasks }}</span>
              </div>
              <el-icon :size="40" color="#e6a23c"><Finished /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-info">
                <span class="label">里程碑</span>
                <span class="value">{{ dashboard.stats.totalMilestones }}</span>
              </div>
              <el-icon :size="40" color="#67c23a"><AlarmClock /></el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-info">
                <span class="label">整体进度</span>
                <span class="value">{{ dashboard.stats.overallProgress }}%</span>
              </div>
              <el-icon :size="40" color="#f56c6c"><DataAnalysis /></el-icon>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :span="16">
          <el-card shadow="hover">
            <template #header>
              <span>最近活动</span>
            </template>
            <el-table v-if="dashboard.recentActivities.length" :data="dashboard.recentActivities" size="small">
              <el-table-column prop="description" label="活动描述" min-width="200" />
              <el-table-column prop="createdAt" label="时间" width="170" />
            </el-table>
            <el-empty v-else description="暂无最近活动" />
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card shadow="hover">
            <template #header>
              <span>快捷操作</span>
            </template>
            <div class="quick-actions">
              <el-button v-permission="'project:create'" type="primary" plain @click="$router.push('/project/list')">
                <el-icon><Plus /></el-icon>新建项目
              </el-button>
              <el-button v-permission="'requirement:create'" type="success" plain @click="$router.push('/requirement')">
                <el-icon><DocumentAdd /></el-icon>新建需求
              </el-button>
              <el-button v-permission="'task:create'" type="warning" plain @click="$router.push('/task')">
                <el-icon><Plus /></el-icon>新建任务
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-else description="请选择一个项目查看看板数据" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProjectDashboard } from '@/api/report'
import { getProjectList } from '@/api/project'
import type { ProjectDashboard } from '@/types/report'
import type { Project } from '@/types/project'

const selectedProjectId = ref<number | null>(null)
const projectList = ref<Project[]>([])
const loading = ref(false)

const dashboard = reactive<ProjectDashboard>({
  projectId: 0,
  projectName: '',
  stats: { totalRequirements: 0, totalTasks: 0, totalMilestones: 0, totalMembers: 0, overallProgress: 0 },
  requirementStats: { total: 0, byStatus: {}, completionRate: 0 },
  taskStats: { total: 0, byStatus: {}, completionRate: 0 },
  milestoneStats: { total: 0, completed: 0, upcoming: [], overdue: [] },
  recentActivities: [],
})

async function loadProjects() {
  try {
    const data = await getProjectList({ page: 1, size: 100 })
    projectList.value = data.records
    if (projectList.value.length > 0) {
      selectedProjectId.value = projectList.value[0].id
      loadDashboard()
    }
  } catch { /* handled */ }
}

async function loadDashboard() {
  if (!selectedProjectId.value) return
  loading.value = true
  try {
    const data = await getProjectDashboard(selectedProjectId.value)
    Object.assign(dashboard, data)
  } catch {
    ElMessage.warning('看板数据加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadProjects() })
</script>

<style scoped lang="scss">
.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .stat-info {
    display: flex;
    flex-direction: column;

    .label {
      font-size: 14px;
      color: #909399;
    }

    .value {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin-top: 8px;
    }
  }
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .el-button {
    width: 100%;
  }
}
</style>
