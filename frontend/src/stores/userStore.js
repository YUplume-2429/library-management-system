import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const readUser = () => {
    const raw = localStorage.getItem('user')
    if (!raw) {
      return null
    }
    try {
      return JSON.parse(raw)
    } catch {
      localStorage.removeItem('user')
      return null
    }
  }

  const user = ref(readUser())
  const token = ref(localStorage.getItem('token') || '')

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ROLE_ADMIN')
  const isStudent = computed(() => user.value?.role === 'ROLE_STUDENT')

  function setUserInfo(userInfo, authToken) {
    user.value = userInfo
    token.value = authToken
    localStorage.setItem('user', JSON.stringify(userInfo))
    localStorage.setItem('token', authToken)
  }

  function logout() {
    user.value = null
    token.value = ''
    localStorage.removeItem('user')
    localStorage.removeItem('token')
  }

  function updateUser(userInfo) {
    user.value = userInfo
    localStorage.setItem('user', JSON.stringify(userInfo))
  }

  return {
    user,
    token,
    isAuthenticated,
    isAdmin,
    isStudent,
    setUserInfo,
    logout,
    updateUser
  }
})
