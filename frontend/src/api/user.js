import request from '@/utils/request'

/**
 * 获取全部用户（仅管理员）
 */
export const getAllUsers = (params = {}) => {
  return request.get('/users', { params })
}

/**
 * 获取当前用户资料
 */
export const getMyProfile = () => {
  return request.get('/users/me')
}

/**
 * 更新当前用户资料
 */
export const updateMyProfile = (profileData) => {
  return request.put('/users/me', profileData)
}

/**
 * 创建用户（仅管理员）
 */
export const createUser = (userData) => {
  return request.post('/users', userData)
}

/**
 * 批量导入用户（仅管理员）
 */
export const importUsers = (users) => {
  return request.post('/users/import', users)
}

/**
 * 删除用户（仅管理员）
 */
export const deleteUser = (userId, force = false) => {
  return request.delete(`/users/${userId}`, {
    params: { force }
  })
}

/**
 * 重置学生密码为 123456（仅管理员）
 */
export const resetStudentPassword = (userId) => {
  return request.put(`/users/${userId}/reset-password`)
}
