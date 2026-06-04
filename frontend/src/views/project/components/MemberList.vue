<template>
  <div class="member-list">
    <div class="toolbar">
      <el-button v-permission="'team:create'" type="primary" size="small" @click="handleAdd">
        <el-icon><Plus /></el-icon>添加成员
      </el-button>
    </div>
    <el-table :data="members" stripe>
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="username" label="账号" width="140" />
      <el-table-column prop="role" label="角色" width="140">
        <template #default="{ row }">
          <el-tag size="small">{{ row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="joinedAt" label="加入时间" width="170" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-permission="'team:delete'" link type="danger" @click="handleRemove(row as ProjectMember)">
            移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProjectMembers, removeProjectMember } from '@/api/project'
import type { ProjectMember } from '@/types/project'

const props = defineProps<{ projectId: number }>()

const members = ref<ProjectMember[]>([])

async function loadMembers() {
  try {
    const data = await getProjectMembers(props.projectId)
    members.value = data
  } catch {
    // 错误已处理
  }
}

function handleAdd() {
  // TODO: 打开添加成员对话框
}

async function handleRemove(row: ProjectMember) {
  try {
    await ElMessageBox.confirm(`确定移除成员「${row.realName}」？`, '提示', {
      type: 'warning',
    })
    await removeProjectMember(props.projectId, row.userId)
    ElMessage.success('移除成功')
    loadMembers()
  } catch {
    // 用户取消
  }
}

onMounted(() => {
  loadMembers()
})
</script>

<style scoped>
.toolbar {
  margin-bottom: 12px;
}
</style>
