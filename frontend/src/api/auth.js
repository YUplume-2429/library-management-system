import request from '@/utils/request'

/**
 * 登录
 */
export const login = (username, password) => {
  return request.post('/auth/login', {
    username,
    password
  })
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request.get('/auth/me')
}
