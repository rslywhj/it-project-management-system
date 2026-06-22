<template>
  <div class="user-manage">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索用户名/姓名"
          prefix-icon="Search"
          clearable
          style="width: 220px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="正常" value="active" />
          <el-option label="禁用" value="disabled" />
        </el-select>
      </div>
      <el-button type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建用户
      </el-button>
    </div>

    <el-table v-loading="loading" :data="userList" stripe>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="(row as SysUser).status === 'active' ? 'success' : 'danger'" size="small">
            {{ (row as SysUser).status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginAt" label="最后登录" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row as SysUser)">编辑</el-button>
          <el-button link type="warning" @click="handleResetPwd(row as SysUser)">重置密码</el-button>
          <el-button link type="danger" @click="handleDelete(row as SysUser)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>

    <!-- 新建/编辑用户对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新建用户'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
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
import { getUserList, createUser, updateUser, deleteUser, resetUserPassword, type SysUser, type UserCreateRequest } from '@/api/system'

const loading = ref(false)
const userList = ref<SysUser[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<UserCreateRequest>({
  username: '', password: '', realName: '', email: '', phone: '',
})
const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const data = await getUserList(queryParams)
    userList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { username: '', password: '', realName: '', email: '', phone: '' })
  dialogVisible.value = true
}

function handleEdit(row: SysUser) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    username: row.username,
    password: '',
    realName: row.realName,
    email: row.email ?? '',
    phone: row.phone ?? '',
  })
  dialogVisible.value = true
}

async function handleResetPwd(row: SysUser) {
  try {
    await ElMessageBox.confirm(`确定重置用户「${row.realName}」的密码为 123456？`, '提示', { type: 'warning' })
    await resetUserPassword(row.id, '123456')
    ElMessage.success('密码已重置为 123456')
  } catch { /* cancelled */ }
}

async function handleDelete(row: SysUser) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.realName}」？`, '提示', { type: 'warning' })
    await deleteUser(row.id)
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
      await updateUser(editingId.value, {
        realName: formData.realName,
        email: formData.email,
        phone: formData.phone,
      })
      ElMessage.success('更新成功')
    } else {
      await createUser(formData)
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
  margin-bottom: 12px;

  .toolbar-left {
    display: flex;
    gap: 8px;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
