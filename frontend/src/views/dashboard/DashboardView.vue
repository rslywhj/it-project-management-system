<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">进行中的项目</span>
              <span class="value">{{ stats.projectCount }}</span>
            </div>
            <el-icon :size="40" color="#409eff"><Folder /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">待处理需求</span>
              <span class="value">{{ stats.pendingRequirements }}</span>
            </div>
            <el-icon :size="40" color="#e6a23c"><Document /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">进行中的任务</span>
              <span class="value">{{ stats.activeTasks }}</span>
            </div>
            <el-icon :size="40" color="#67c23a"><Finished /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">即将到期里程碑</span>
              <span class="value">{{ stats.upcomingMilestones }}</span>
            </div>
            <el-icon :size="40" color="#f56c6c"><AlarmClock /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>我的待办</span>
          </template>
          <el-table v-if="stats.recentTasks.length" :data="stats.recentTasks" size="small">
            <el-table-column prop="title" label="任务标题" min-width="200" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deadline" label="截止日期" width="120" />
          </el-table>
          <el-empty v-else description="暂无待办事项" />
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
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { getDashboardStats, type DashboardStats } from '@/api/system'

const stats = reactive<DashboardStats>({
  projectCount: 0,
  pendingRequirements: 0,
  activeTasks: 0,
  upcomingMilestones: 0,
  recentTasks: [],
})

const statusLabelMap: Record<string, string> = { todo: '待办', in_progress: '进行中', done: '已完成' }
const statusTypeMap: Record<string, 'info' | 'warning' | 'success'> = { todo: 'info', in_progress: 'warning', done: 'success' }

function statusLabel(s: string) { return statusLabelMap[s] ?? s }
function statusType(s: string) { return statusTypeMap[s] ?? 'info' }

async function loadStats() {
  try {
    const data = await getDashboardStats()
    Object.assign(stats, data)
  } catch {
    // 降级：保持默认0值
  }
}

onMounted(() => { loadStats() })
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
