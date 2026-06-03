<template>
  <div class="dashboard-page">
    <el-card>
      <template #header>
        <div class="title">Dashboard</div>
      </template>

      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="info-card">
            <div class="label">Username</div>
            <div class="value">{{ userStore.user?.username || '-' }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="info-card">
            <div class="label">Name</div>
            <div class="value">{{ userStore.user?.name || '-' }}</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="info-card">
            <div class="label">Role</div>
            <div class="value">
              <el-tag :type="userStore.isAdmin ? 'danger' : 'success'">
                {{ userStore.user?.role || '-' }}
              </el-tag>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="info-card">
            <div class="label">Status</div>
            <div class="value">Logged in</div>
          </div>
        </el-col>
      </el-row>

      <el-divider />

      <div class="actions">
        <el-button type="primary" @click="router.push('/books')">Open Books</el-button>
        <el-button v-if="userStore.isStudent" type="warning" @click="router.push('/borrows')">My Borrows</el-button>
        <el-button v-if="userStore.isAdmin" type="success" @click="router.push('/admin')">Open Admin</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.title {
  font-size: 18px;
  font-weight: 700;
}

.info-card {
  padding: 20px;
  border-radius: 8px;
  background: #f5f7fa;
  text-align: center;
}

.label {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}

.value {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
