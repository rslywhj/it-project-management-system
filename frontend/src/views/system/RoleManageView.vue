<template>
  <div class="role-manage">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索角色名称"
        prefix-icon="Search"
        clearable
        style="width: 220px"
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      />
      <el-button type="primary" size="small" @click="handleCreate">
        <el-icon><Plus /></el-icon>新建角色
      </el-button>
    </div>

    <el-table v-loading="loading" :data="roleList" stripe>
      <el-table-column prop="roleCode" label="角色编码" width="140" />
      <el-table-column prop="roleName" label="角色名称" width="140" />
      <el-table-column prop="description" label="说明" min-width="200" />
      <el-table-column prop="dataScope" label="数据范围" width="120">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ dataScopeLabel((row as SysRole).dataScope) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="(row as SysRole).status === 'active' ? 'success' : 'danger'" size="small">
            {{ (row as SysRole).status === 'active' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isSystem" label="内置" width="70">
        <template #default="{ row }">
          <el-tag v-if="(row as SysRole).isSystem" size="small" type="warning">是</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row as SysRole)">编辑</el-button>
          <el-button link type="danger" :disabled="!!(row as SysRole).isSystem" @click="handleDelete(row as SysRole)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </div>

    <!-- 新建/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新建角色'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="80px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" placeholder="如 admin、pm、dev" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="如 管理员、项目经理" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="角色说明" />
        </el-form-item>
        <el-form-item label="数据范围">
          <el-select v-model="formData.dataScope" style="width: 100%">
            <el-option label="全部数据" value="all" />
            <el-option label="项目级" value="project" />
            <el-option label="推广单元级" value="promotion_unit" />
            <el-option label="仅本人" value="self" />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, type SysRole, type RoleCreateRequest } from '@/api/system'

const loading = ref(false)
const roleList = ref<SysRole[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const editingId = ref<number | null>(null)
const formData = reactive<RoleCreateRequest>({
  roleCode: '', roleName: '', description: '', dataScope: 'project', sortOrder: 0,
})
const formRules: FormRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

const dataScopeMap: Record<string, string> = {
  all: '全部数据', project: '项目级', promotion_unit: '推广单元级', self: '仅本人',
}
function dataScopeLabel(s: string) { return dataScopeMap[s] ?? s }

async function loadData() {
  loading.value = true
  try {
    const data = await getRoleList({ page: page.value, size: size.value, keyword: keyword.value })
    roleList.value = data.records
    total.value = data.total
  } catch { /* handled */ } finally {
    loading.value = false
  }
}

function handleSearch() { page.value = 1; loadData() }

function handleCreate() {
  isEdit.value = false
  editingId.value = null
  Object.assign(formData, { roleCode: '', roleName: '', description: '', dataScope: 'project', sortOrder: 0 })
  dialogVisible.value = true
}

function handleEdit(row: SysRole) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(formData, {
    roleCode: row.roleCode,
    roleName: row.roleName,
    description: row.description ?? '',
    dataScope: row.dataScope,
    sortOrder: row.sortOrder,
  })
  dialogVisible.value = true
}

async function handleDelete(row: SysRole) {
  try {
    await ElMessageBox.confirm(`确定删除角色「${row.roleName}」？`, '提示', { type: 'warning' })
    await deleteRole(row.id)
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
      await updateRole(editingId.value, {
        roleName: formData.roleName,
        description: formData.description,
        dataScope: formData.dataScope,
        sortOrder: formData.sortOrder,
      })
      ElMessage.success('更新成功')
    } else {
      await createRole(formData)
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
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
</style>
