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

    <!-- 添加成员对话框 -->
    <el-dialog v-model="addDialogVisible" title="添加成员" width="400px" destroy-on-close>
      <el-form ref="addFormRef" :model="addFormData" :rules="addFormRules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="addFormData.userId" :min="1" placeholder="请输入用户ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="addFormData.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="开发者" value="developer" />
            <el-option label="测试员" value="tester" />
            <el-option label="观察者" value="viewer" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAddSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getProjectMembers, addProjectMember, removeProjectMember } from '@/api/project'
import type { ProjectMember } from '@/types/project'

const props = defineProps<{ projectId: number }>()

const members = ref<ProjectMember[]>([])

// 添加成员对话框
const addDialogVisible = ref(false)
const addLoading = ref(false)
const addFormRef = ref<FormInstance>()
const addFormData = reactive({
  userId: undefined as number | undefined,
  role: 'developer',
})
const addFormRules: FormRules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

async function loadMembers() {
  try {
    const data = await getProjectMembers(props.projectId)
    members.value = data
  } catch {
    // 错误已处理
  }
}

function handleAdd() {
  addFormData.userId = undefined
  addFormData.role = 'developer'
  addDialogVisible.value = true
}

async function handleAddSubmit() {
  const valid = await addFormRef.value?.validate().catch(() => false)
  if (!valid || !addFormData.userId) return
  addLoading.value = true
  try {
    await addProjectMember(props.projectId, { userId: addFormData.userId, role: addFormData.role })
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadMembers()
  } catch {
    // 错误已由拦截器处理
  } finally {
    addLoading.value = false
  }
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
