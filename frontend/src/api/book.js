import request from '@/utils/request'

/**
 * 获取所有图书
 */
export const getAllBooks = () => {
  return request.get('/books')
}

/**
 * 获取单本图书详情
 */
export const getBookById = (bookId) => {
  return request.get(`/books/${bookId}`)
}

/**
 * 搜索图书（按书名）
 */
export const searchBooks = (keyword) => {
  return request.get('/books/search', {
    params: { keyword }
  })
}

/**
 * 获取所有可用的图书
 */
export const getAvailableBooks = () => {
  return request.get('/books/available')
}

/**
 * 获取全部图书分类
 */
export const getCategories = () => {
  return request.get('/books/categories')
}

/**
 * 按分类查询图书
 */
export const getBooksByCategory = (category) => {
  return request.get('/books/category', {
    params: { category }
  })
}

/**
 * 创建新图书（仅管理员）
 */
export const createBook = (bookData) => {
  return request.post('/books', bookData)
}

/**
 * 批量导入图书（仅管理员）
 */
export const importBooks = (books) => {
  return request.post('/books/import', books)
}

/**
 * 更新图书（仅管理员）
 */
export const updateBook = (bookId, bookData) => {
  return request.put(`/books/${bookId}`, bookData)
}

/**
 * 删除图书（仅管理员）
 */
export const deleteBook = (bookId) => {
  return request.delete(`/books/${bookId}`)
}
