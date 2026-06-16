<template>
  <div class="role-list">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索角色名称"
          prefix-icon="Search"
          clearable
          style="width: 200px"
        />
      </div>
      <el-button v-permission="'system:role'" type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建角色
      </el-button>
    </div>

    <el-table v-loading="loading" :data="filteredRoles" stripe>
      <el-table-column prop="roleCode" label="角色编码" width="160" />
      <el-table-column prop="roleName" label="角色名称" width="140" />
      <el-table-column prop="description" label="说明" min-width="200" />
      <el-table-column prop="dataScope" label="数据范围" width="120">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ dataScopeLabel(row.dataScope) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isSystem" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.isSystem ? 'warning' : 'info'" size="small">
            {{ row.isSystem ? '内置' : '自定义' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
            {{ row.status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'system:role'" link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button v-permission="'system:role'" link type="info" @click="handlePermission(row)">权限</el-button>
          <el-button
            v-permission="'system:role'"
            link
            type="danger"
            :disabled="!!row.isSystem"
            @click="handleDelete(row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新建/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新建角色'" width="480px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" placeholder="如：project_manager" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="如：项目经理" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="角色说明" />
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="formData.dataScope" style="width: 100%">
            <el-option label="全部数据" value="all" />
            <el-option label="项目数据" value="project" />
            <el-option label="推广单元数据" value="promotion_unit" />
            <el-option label="仅本人数据" value="self" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog v-model="permDialogVisible" title="分配权限" width="500px" destroy-on-close>
      <div v-loading="permLoading">
        <el-tree
          ref="permTreeRef"
          :data="permissionTree"
          show-checkbox
          node-key="id"
          :default-checked-keys="checkedPermIds"
          :props="{ children: 'children', label: 'label' }"
        />
      </div>
      <template #footer>
        <el-button @click="permDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="permSubmitLoading" @click="handlePermSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole } from '@/api/system'
import type { SysRole, RoleRequest } from '@/types/system'

const loading = ref(false)
const roleList = ref<SysRole[]>([])
const searchKeyword = ref('')

const filteredRoles = computed(() => {
  if (!searchKeyword.value) return roleList.value
  const kw = searchKeyword.value.toLowerCase()
  return roleList.value.filter(r =>
    r.roleName.toLowerCase().includes(kw) || r.roleCode.toLowerCase().includes(kw)
  )
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<RoleRequest>({
  roleCode: '',
  roleName: '',
  description: '',
  dataScope: 'self',
  sortOrder: 0,
  permissionIds: [],
})
const formRules: FormRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

const permDialogVisible = ref(false)
const permLoading = ref(false)
const permSubmitLoading = ref(false)
const permTreeRef = ref()
const checkedPermIds = ref<number[]>([])
const editingRoleId = ref<number | null>(null)

// 权限树（按模块分组）
const permissionTree = ref<any[]>([])

const dataScopeMap: Record<string, string> = {
  all: '全部数据',
  project: '项目数据',
  promotion_unit: '推广单元数据',
  self: '仅本人数据',
}
function dataScopeLabel(s: string) { return dataScopeMap[s] ?? s }

async function loadData() {
  loading.value = true
  try {
    roleList.value = await getRoleList()
  } catch { /* handled */ } finally { loading.value = false }
}

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { roleCode: '', roleName: '', description: '', dataScope: 'self', sortOrder: 0, permissionIds: [] })
  dialogVisible.value = true
}

function handleEdit(row: SysRole) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    roleCode: row.roleCode,
    roleName: row.roleName,
    description: row.description,
    dataScope: row.dataScope,
    sortOrder: row.sortOrder,
    permissionIds: row.permissionIds ?? [],
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value && editingId.value) {
      await updateRole(editingId.value, formData)
      ElMessage.success('更新成功')
    } else {
      await createRole(formData)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { /* handled */ } finally { submitLoading.value = false }
}

async function handlePermission(row: SysRole) {
  editingRoleId.value = row.id
  checkedPermIds.value = row.permissionIds ?? []
  // 加载权限树（从后端获取或本地构建）
  await loadPermissionTree()
  permDialogVisible.value = true
}

async function loadPermissionTree() {
  permLoading.value = true
  try {
    // 使用预定义的权限树结构（与后端 seed data 一致）
    permissionTree.value = buildPermissionTree()
  } catch { /* handled */ } finally { permLoading.value = false }
}

function buildPermissionTree() {
  // 权限模块和操作定义（与 V002 seed data 一致）
  const modules: Record<string, { label: string; permissions: { id: number; code: string; name: string }[] }> = {
    project: {
      label: '项目管理',
      permissions: [
        { id: 1, code: 'project:view', name: '查看项目' },
        { id: 2, code: 'project:create', name: '创建项目' },
        { id: 3, code: 'project:edit', name: '编辑项目' },
        { id: 4, code: 'project:delete', name: '删除项目' },
        { id: 5, code: 'project:manage', name: '管理项目' },
      ],
    },
    team: {
      label: '团队管理',
      permissions: [
        { id: 6, code: 'team:view', name: '查看团队' },
        { id: 7, code: 'team:add', name: '添加成员' },
        { id: 8, code: 'team:remove', name: '移除成员' },
        { id: 9, code: 'team:assign', name: '分配角色' },
      ],
    },
    requirement: {
      label: '需求管理',
      permissions: [
        { id: 10, code: 'requirement:view', name: '查看需求' },
        { id: 11, code: 'requirement:create', name: '创建需求' },
        { id: 12, code: 'requirement:edit', name: '编辑需求' },
        { id: 13, code: 'requirement:delete', name: '删除需求' },
        { id: 14, code: 'requirement:manage', name: '管理需求' },
      ],
    },
    task: {
      label: '任务管理',
      permissions: [
        { id: 15, code: 'task:view', name: '查看任务' },
        { id: 16, code: 'task:create', name: '创建任务' },
        { id: 17, code: 'task:edit', name: '编辑任务' },
        { id: 18, code: 'task:delete', name: '删除任务' },
        { id: 19, code: 'task:manage', name: '管理任务' },
      ],
    },
    system: {
      label: '系统管理',
      permissions: [
        { id: 49, code: 'system:user', name: '用户管理' },
        { id: 50, code: 'system:role', name: '角色管理' },
        { id: 51, code: 'system:permission', name: '权限管理' },
        { id: 52, code: 'system:dict', name: '字典管理' },
        { id: 53, code: 'system:org', name: '组织管理' },
        { id: 54, code: 'system:log', name: '操作日志' },
      ],
    },
  }

  return Object.entries(modules).map(([key, mod]) => ({
    id: `module_${key}`,
    label: mod.label,
    children: mod.permissions.map(p => ({
      id: p.id,
      label: `${p.name} (${p.code})`,
    })),
  }))
}

async function handlePermSubmit() {
  if (!editingRoleId.value) return
  permSubmitLoading.value = true
  try {
    const checkedKeys = permTreeRef.value.getCheckedKeys(true)
    const role = roleList.value.find(r => r.id === editingRoleId.value)
    if (role) {
      await updateRole(editingRoleId.value, {
        roleCode: role.roleCode,
        roleName: role.roleName,
        description: role.description,
        dataScope: role.dataScope,
        permissionIds: checkedKeys.filter((k: any) => typeof k === 'number'),
      })
      ElMessage.success('权限更新成功')
      loadData()
    }
    permDialogVisible.value = false
  } catch { /* handled */ } finally { permSubmitLoading.value = false }
}

async function handleDelete(row: SysRole) {
  try {
    await ElMessageBox.confirm(`确定删除角色「${row.roleName}」？此操作不可恢复。`, '提示', { type: 'error' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => { loadData() })
</script>

<style scoped lang="scss">
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; .toolbar-left { display: flex; gap: 8px; } }
</style>
