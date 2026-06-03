import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

// 导入页面组件
import LoginView from '@/views/LoginView.vue'
import DashboardView from '@/views/DashboardView.vue'
import BooksView from '@/views/BooksView.vue'
import BorrowRecordsView from '@/views/BorrowRecordsView.vue'
import MyProfileView from '@/views/MyProfileView.vue'
import AdminView from '@/views/AdminView.vue'
import NotFoundView from '@/views/NotFoundView.vue'

/**
 * 路由配置
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: DashboardView,
    meta: { requiresAuth: true }
  },
  {
    path: '/books',
    name: 'Books',
    component: BooksView,
    meta: { requiresAuth: false }
  },
  {
    path: '/borrows',
    name: 'Borrows',
    component: BorrowRecordsView,
    meta: { requiresAuth: true, requiresRole: 'ROLE_STUDENT' }
  },
  {
    path: '/my',
    name: 'MyProfile',
    component: MyProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: AdminView,
    meta: { requiresAuth: true, requiresRole: 'ROLE_ADMIN' }
  },
  {
    path: '/',
    redirect: '/books'
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFoundView
  }
]

/**
 * 创建路由器
 */
const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 路由守卫 - 权限验证
 */
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.isAuthenticated
  const userRole = userStore.user?.role
  
  // 检查是否需要身份验证
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 需要登录但未登录，重定向到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  
  // 检查是否需要特定角色
  if (to.meta.requiresRole && userRole !== to.meta.requiresRole) {
    // 权限不足，重定向到首页
    next('/books')
    return
  }
  
  // 如果已登录且页面是登录页，重定向到首页
  if (isAuthenticated && to.path === '/login') {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
