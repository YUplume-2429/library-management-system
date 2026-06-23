<template>
  <el-config-provider :locale="elementLocale">
    <div class="app-shell">
      <header class="app-header">
        <div class="brand">Library System</div>

        <el-menu class="nav" mode="horizontal" :default-active="activeMenu" @select="handleMenuSelect">
          <el-menu-item index="/books">Books</el-menu-item>
          <el-menu-item v-if="userStore.isAuthenticated" index="/dashboard">Dashboard</el-menu-item>
          <el-menu-item v-if="userStore.isStudent" index="/borrows">My Borrows</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin">Admin</el-menu-item>
          <el-menu-item v-if="userStore.isAuthenticated" index="/my">my</el-menu-item>
        </el-menu>

        <div class="user-panel">
          <el-select
            v-model="language"
            class="language-select"
            aria-label="Language"
            size="small"
            @change="setLanguage"
          >
            <el-option label="English" value="en" />
            <el-option label="中文" value="zh" />
          </el-select>

          <template v-if="userStore.isAuthenticated">
            <span class="user-meta">{{ userStore.user?.name }} / {{ userStore.user?.role }}</span>
            <el-button type="danger" text @click="handleLogout">Logout</el-button>
          </template>
          <el-button v-else type="primary" @click="handleLogin">Login</el-button>
        </div>
      </header>

      <main class="app-main">
        <router-view />
      </main>
    </div>
  </el-config-provider>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import en from 'element-plus/es/locale/lang/en'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { useI18n } from './i18n'
import { useUserStore } from './stores/userStore'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { language, setLanguage, t } = useI18n()
const elementLocale = computed(() => (language.value === 'zh' ? zhCn : en))

const activeMenu = computed(() => {
  if (route.path.startsWith('/admin')) {
    return '/admin'
  }
  if (route.path.startsWith('/dashboard')) {
    return '/dashboard'
  }
  if (route.path.startsWith('/borrows')) {
    return '/borrows'
  }
  if (route.path.startsWith('/my')) {
    return '/my'
  }
  return '/books'
})

const handleMenuSelect = (index) => {
  router.push(index)
}

const handleLogin = () => {
  router.push('/login')
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success(t('Logged out'))
  router.push('/login')
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: #f5f7fa;
}

.app-header {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  padding: 0 20px;
  min-height: 64px;
  background: #ffffff;
  border-bottom: 1px solid #ebeef5;
}

.brand {
  flex: 0 0 auto;
  font-weight: 700;
  color: #409eff;
}

.nav {
  flex: 1;
  min-width: 0;
}

.user-panel {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.language-select {
  width: 104px;
}

.user-meta {
  color: #606266;
  font-size: 14px;
}

.app-main {
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
}
</style>
