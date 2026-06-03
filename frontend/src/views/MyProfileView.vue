<template>
  <div class="profile-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <div>
            <div class="title">My Profile</div>
            <div class="subtitle">View your account and update allowed information.</div>
          </div>
          <el-button type="primary" :loading="loading" @click="loadProfile">Refresh</el-button>
        </div>
      </template>

      <el-skeleton v-if="loading" :rows="6" animated />

      <template v-else>
        <el-descriptions v-if="profile" :column="1" border class="profile-summary">
          <el-descriptions-item label="Username">{{ profile.username }}</el-descriptions-item>
          <el-descriptions-item label="Role">
            <el-tag :type="profile.roleName === 'ROLE_ADMIN' ? 'danger' : 'success'">
              {{ profile.roleName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Status">
            <el-tag :type="profile.isActive ? 'success' : 'info'">
              {{ profile.isActive ? 'Active' : 'Disabled' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Created At">{{ profile.createdAt || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-form :model="form" label-width="128px" class="profile-form">
          <el-form-item label="Name">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="Email">
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="Current Password">
            <el-input v-model="form.currentPassword" type="password" show-password placeholder="Required only when changing password" />
          </el-form-item>
          <el-form-item label="New Password">
            <el-input v-model="form.newPassword" type="password" show-password placeholder="Leave blank to keep current password" />
          </el-form-item>
          <el-form-item label="Confirm Password">
            <el-input v-model="confirmPassword" type="password" show-password placeholder="Repeat new password" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">Save Changes</el-button>
          </el-form-item>
        </el-form>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyProfile, updateMyProfile } from '@/api/user'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const loading = ref(false)
const saving = ref(false)
const profile = ref(null)
const confirmPassword = ref('')

const form = ref({
  name: '',
  email: '',
  currentPassword: '',
  newPassword: ''
})

const syncForm = (data) => {
  profile.value = data
  form.value = {
    name: data?.name || '',
    email: data?.email || '',
    currentPassword: '',
    newPassword: ''
  }
  confirmPassword.value = ''
}

const loadProfile = async () => {
  loading.value = true
  try {
    const response = await getMyProfile()
    if (response.code === 200) {
      syncForm(response.data)
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load profile')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!form.value.name) {
    ElMessage.error('Name cannot be empty')
    return
  }
  if (form.value.newPassword && form.value.newPassword !== confirmPassword.value) {
    ElMessage.error('New password confirmation does not match')
    return
  }

  saving.value = true
  try {
    const payload = {
      name: form.value.name,
      email: form.value.email,
      currentPassword: form.value.currentPassword,
      newPassword: form.value.newPassword
    }
    const response = await updateMyProfile(payload)
    if (response.code === 200) {
      ElMessage.success('Profile updated successfully')
      syncForm(response.data)
      userStore.updateUser({
        ...userStore.user,
        name: response.data.name,
        email: response.data.email
      })
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Save profile failed')
  } finally {
    saving.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.profile-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.title {
  font-size: 18px;
  font-weight: 700;
}

.subtitle {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.profile-summary {
  margin-bottom: 16px;
}

.profile-form {
  max-width: 680px;
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
