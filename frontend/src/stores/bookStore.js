import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 图书状态管理
 */
export const useBookStore = defineStore('book', () => {
  // ============ 状态 ============
  const books = ref([])
  const currentBook = ref(null)
  const loading = ref(false)
  const searchKeyword = ref('')
  
  // ============ 方法 ============
  
  /**
   * 设置图书列表
   */
  function setBooks(bookList) {
    books.value = bookList
  }
  
  /**
   * 设置当前图书
   */
  function setCurrentBook(book) {
    currentBook.value = book
  }
  
  /**
   * 清除当前图书
   */
  function clearCurrentBook() {
    currentBook.value = null
  }
  
  /**
   * 设置加载状态
   */
  function setLoading(isLoading) {
    loading.value = isLoading
  }
  
  /**
   * 设置搜索关键词
   */
  function setSearchKeyword(keyword) {
    searchKeyword.value = keyword
  }
  
  /**
   * 添加图书到列表
   */
  function addBook(book) {
    books.value.unshift(book)
  }
  
  /**
   * 更新图书列表中的图书
   */
  function updateBook(updatedBook) {
    const index = books.value.findIndex(b => b.bookId === updatedBook.bookId)
    if (index !== -1) {
      books.value[index] = updatedBook
    }
  }
  
  /**
   * 删除图书
   */
  function deleteBook(bookId) {
    books.value = books.value.filter(b => b.bookId !== bookId)
  }
  
  return {
    // 状态
    books,
    currentBook,
    loading,
    searchKeyword,
    // 方法
    setBooks,
    setCurrentBook,
    clearCurrentBook,
    setLoading,
    setSearchKeyword,
    addBook,
    updateBook,
    deleteBook
  }
})
