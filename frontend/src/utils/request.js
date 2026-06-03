import axios from 'axios'

/**
 * Axios 实例配置
 */
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

/**
 * 请求拦截器 - 添加 Authorization 令牌
 */
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器 - 处理错误
 */
service.interceptors.response.use(
  response => {
    const res = response.data
    // 假设服务器返回的结构中，code、message、data 为关键字段
    if (res.code !== 200) {
      // 处理非200的响应
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    // 处理响应错误
    if (error.response?.status === 401) {
      const requestUrl = error.config?.url || ''
      if (!requestUrl.includes('/auth/login')) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default service
