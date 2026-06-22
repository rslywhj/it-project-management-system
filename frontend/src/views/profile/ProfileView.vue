<template>
  <div class="profile-page">
    <el-row :gutter="16">
      <!-- 个人信息 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <span>个人信息</span>
          </template>
          <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="80px" style="max-width: 480px">
            <el-form-item label="用户名">
              <el-input :model-value="userStore.username" disabled />
            </el-form-item>
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 头像 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <span>头像</span>
          </template>
          <div class="avatar-section">
            <el-avatar :size="96" icon="UserFilled" />
            <el-button size="small" style="margin-top: 12px" disabled>更换头像</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 修改密码 -->
    <el-card shadow="hover" style="margin-top: 16px">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="80px" style="max-width: 480px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="pwdLoading" @click="handleChangePassword">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { updateProfile, changePassword, getProfile } from '@/api/profile'

const userStore = useUserStore()

// ─── 个人信息表单 ─────────────────────────────────────────────
const profileFormRef = ref<FormInstance>()
const profileLoading = ref(false)
const profileForm = reactive({
  realName: '',
  email: '',
  phone: '',
})
const profileRules: FormRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

// ─── 密码表单 ─────────────────────────────────────────────────
const pwdFormRef = ref<FormInstance>()
const pwdLoading = ref(false)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})
const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ],
}

async function loadProfile() {
  try {
    const info = await getProfile() as Record<string, unknown>
    profileForm.realName = (info.realName as string) ?? ''
    profileForm.email = (info.email as string) ?? ''
    profileForm.phone = (info.phone as string) ?? ''
  } catch { /* handled */ }
}

async function handleUpdateProfile() {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return
  profileLoading.value = true
  try {
    await updateProfile(profileForm)
    ElMessage.success('个人信息已更新')
    // 同步到 store
    if (userStore.userInfo) {
      userStore.userInfo.realName = profileForm.realName
    }
  } catch { /* handled */ } finally {
    profileLoading.value = false
  }
}

async function handleChangePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return
  pwdLoading.value = true
  try {
    await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
    })
    ElMessage.success('密码修改成功，请重新登录')
    // 清空表单
    Object.assign(pwdForm, { oldPassword: '', newPassword: '', confirmPassword: '' })
  } catch { /* handled */ } finally {
    pwdLoading.value = false
  }
}

onMounted(() => { loadProfile() })
</script>

<style scoped lang="scss">
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 0;
}
</style>
