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
        <el-tab-pane label="项目看板" name="dashboard">
          <ProjectDashboard :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="需求管理" name="requirement">
          <RequirementList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="任务管理" name="task">
          <TaskList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="里程碑" name="milestone">
          <MilestoneList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane v-if="project?.promotionEnabled" label="推广管理" name="promotion">
          <PromotionUnitList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="测试管理" name="test">
          <TestPlanList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="交付物" name="delivery">
          <DeliveryList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="风险与问题" name="risk">
          <RiskList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="资源工时" name="resource">
          <ResourceList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="知识库" name="knowledge">
          <KnowledgeList :project-id="projectId" />
        </el-tab-pane>
        <el-tab-pane label="团队成员" name="member">
          <MemberList :project-id="projectId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getProjectDetail } from '@/api/project'
import type { Project } from '@/types/project'
import RequirementList from '@/views/requirement/RequirementListView.vue'
import TaskList from '@/views/task/TaskListView.vue'
import MilestoneList from '@/views/milestone/MilestoneListView.vue'
import PromotionUnitList from '@/views/promotion/PromotionUnitListView.vue'
import TestPlanList from '@/views/test/TestPlanListView.vue'
import DeliveryList from '@/views/delivery/DeliveryListView.vue'
import ProjectDashboard from '@/views/report/ProjectDashboardView.vue'
import RiskList from '@/views/risk/RiskListView.vue'
import ResourceList from '@/views/resource/ResourceListView.vue'
import KnowledgeList from '@/views/knowledge/KnowledgeListView.vue'
import MemberList from './components/MemberList.vue'

const route = useRoute()
const projectId = Number(route.params.id)

const loading = ref(false)
const project = ref<Project | null>(null)
const activeTab = ref('requirement')

const statusMap: Record<string, { label: string; type: 'info' | 'warning' | 'success' | 'danger' }> = {
  planning: { label: '规划中', type: 'info' },
  active: { label: '进行中', type: 'info' },
  suspended: { label: '已暂停', type: 'warning' },
  completed: { label: '已完成', type: 'success' },
  cancelled: { label: '已取消', type: 'danger' },
}

function statusLabel(status: string) {
  return statusMap[status]?.label ?? status
}

function statusTagType(status: string) {
  return statusMap[status]?.type ?? 'info'
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
  // TODO: 打开编辑对话框
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
