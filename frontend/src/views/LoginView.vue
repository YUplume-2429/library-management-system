<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        <div class="title">Login</div>
      </template>

      <el-form :model="form" label-width="72px" @submit.prevent="handleLogin">
        <el-form-item label="Username">
          <el-input v-model="form.username" placeholder="Enter username" clearable />
        </el-form-item>

        <el-form-item label="Password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="Enter password"
            clearable
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <div class="actions">
          <el-button type="primary" :loading="loading" @click="handleLogin">Login</el-button>
          <el-button @click="handleReset">Reset</el-button>
        </div>
      </el-form>

      <el-divider />
      <div class="tips">
        <div>Admin: admin / admin123</div>
        <div>Student: student1 / student123</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const form = ref({
  username: 'admin',
  password: 'admin123'
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.error('Please enter username and password')
    return
  }

  loading.value = true
  try {
    const response = await login(form.value.username, form.value.password)
    if (response.code === 200 && response.data) {
      userStore.setUserInfo(response.data, response.data.token)
      ElMessage.success('Login success')
      const redirect = router.currentRoute.value.query.redirect
      const target = typeof redirect === 'string' ? redirect : '/dashboard'
      router.push(target)
      return
    }
    ElMessage.error(response.message || 'Login failed')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Login failed')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  form.value = {
    username: 'admin',
    password: 'admin123'
  }
}
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 64px);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef3ff 0%, #f6f7fb 100%);
}

.login-card {
  width: 100%;
  max-width: 420px;
}

.title {
  font-size: 20px;
  font-weight: 700;
  text-align: center;
}

.actions {
  display: flex;
  gap: 12px;
}

.actions .el-button {
  flex: 1;
}

.tips {
  color: #606266;
  font-size: 13px;
  line-height: 1.8;
}
</style>
