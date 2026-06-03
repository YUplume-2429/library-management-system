import request from '@/utils/request'

/**
 * 借阅图书
 */
export const borrowBook = (bookId) => {
  return request.post(`/borrows/books/${bookId}`)
}

/**
 * 归还图书
 */
export const returnBook = (recordId) => {
  return request.put(`/borrows/${recordId}/return`)
}

/**
 * 获取当前用户借阅记录
 */
export const getMyBorrowRecords = () => {
  return request.get('/borrows/my')
}

/**
 * 管理员获取全部借阅记录
 */
export const getAllBorrowRecords = () => {
  return request.get('/borrows')
}
