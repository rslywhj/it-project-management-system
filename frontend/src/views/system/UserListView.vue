<template>
  <div class="user-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索用户名/姓名/邮箱"
          prefix-icon="Search"
          clearable
          style="width: 240px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="正常" value="active" />
          <el-option label="禁用" value="disabled" />
          <el-option label="锁定" value="locked" />
        </el-select>
      </div>
      <el-button v-permission="'system:user'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建用户
      </el-button>
    </div>

    <el-table v-loading="loading" :data="userList" stripe>
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column label="角色" min-width="150">
        <template #default="{ row }">
          <el-tag v-for="role in row.roles" :key="role.id" size="small" style="margin-right: 4px">
            {{ role.roleName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastLoginAt" label="最后登录" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'system:user'" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-permission="'system:user'" link :type="row.status === 'active' ? 'warning' : 'success'" @click="handleToggleStatus(row)">
            {{ row.status === 'active' ? '禁用' : '启用' }}
          </el-button>
          <el-button v-permission="'system:user'" link type="danger" @click="handleDelete(row)">删除</el-button>
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
        <el-form-item v-if="!isEdit" label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input v-model="formData.password" type="password" placeholder="登录密码" show-password />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="邮箱地址" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="手机号码" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formData.roleIds" multiple placeholder="选择角色" style="width: 100%">
            <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="备注信息" />
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
import { getUserList, createUser, updateUser, deleteUser, updateUserStatus } from '@/api/system'
import { getRoleList } from '@/api/system'
import type { SysUser, UserCreateRequest, UserUpdateRequest, SysRole } from '@/types/system'

const loading = ref(false)
const userList = ref<SysUser[]>([])
const total = ref(0)
const queryParams = reactive({ page: 1, size: 10, keyword: '', status: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<UserCreateRequest & UserUpdateRequest>({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  remark: '',
  roleIds: [],
})
const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const roleOptions = ref<SysRole[]>([])

const statusMap: Record<string, string> = { active: '正常', disabled: '禁用', locked: '锁定' }
const statusTypeMap: Record<string, 'success' | 'danger' | 'warning'> = { active: 'success', disabled: 'danger', locked: 'warning' }

function statusLabel(s: string) { return statusMap[s] ?? s }
function statusType(s: string): 'success' | 'danger' | 'warning' { return statusTypeMap[s] ?? 'warning' }

async function loadData() {
  loading.value = true
  try {
    const data = await getUserList(queryParams)
    userList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally { loading.value = false }
}

async function loadRoles() {
  try {
    roleOptions.value = await getRoleList()
  } catch { /* handled */ }
}

function handleSearch() { queryParams.page = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { username: '', password: '', realName: '', email: '', phone: '', remark: '', roleIds: [] })
  dialogVisible.value = true
}

function handleEdit(row: SysUser) {
  isEdit.value = true
  editingId.value = row.userId
  Object.assign(formData, {
    realName: row.realName,
    email: row.email,
    phone: row.phone,
    remark: row.remark,
    roleIds: row.roles?.map(r => r.id) ?? [],
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateUser(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createUser(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handleToggleStatus(row: SysUser) {
  const newStatus = row.status === 'active' ? 'disabled' : 'active'
  const label = newStatus === 'active' ? '启用' : '禁用'
  try {
    await ElMessageBox.confirm(`确定${label}用户「${row.username}」？`, '提示', { type: 'warning' })
    await updateUserStatus(row.userId, newStatus)
    ElMessage.success(`${label}成功`)
    loadData()
  } catch { /* cancelled */ }
}

async function handleDelete(row: SysUser) {
  try {
    await ElMessageBox.confirm(`确定删除用户「${row.username}」？此操作不可恢复。`, '提示', { type: 'error' })
    await deleteUser(row.userId)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => {
  loadData()
  loadRoles()
})
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
.pagination-wrapper { display: flex; justify-content: flex-end; margin-top: 12px; }
</style>
