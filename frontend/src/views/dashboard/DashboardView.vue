<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <span class="label">进行中的项目</span>
              <span class="value">0</span>
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
              <span class="value">0</span>
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
              <span class="value">0</span>
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
              <span class="value">0</span>
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
          <el-empty description="暂无待办事项" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>快捷操作</span>
          </template>
          <div class="quick-actions">
            <el-button v-permission="'project:create'" type="primary" plain>
              <el-icon><Plus /></el-icon>新建项目
            </el-button>
            <el-button v-permission="'requirement:create'" type="success" plain>
              <el-icon><DocumentAdd /></el-icon>新建需求
            </el-button>
            <el-button v-permission="'task:create'" type="warning" plain>
              <el-icon><Plus /></el-icon>新建任务
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
// Dashboard — 第一期使用占位数据，后续接入真实 API
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
