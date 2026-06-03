<template>
  <div class="books-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <div class="title">Books</div>
          <div class="filters">
            <el-input
              v-model="keyword"
              placeholder="Search by title"
              clearable
              @clear="handleSearch"
              @input="handleSearchDebounced"
            />
            <el-select
              v-model="selectedCategory"
              placeholder="All categories"
              clearable
              @change="handleSearch"
              @clear="handleSearch"
            >
              <el-option
                v-for="category in categories"
                :key="category"
                :label="category"
                :value="category"
              />
            </el-select>
          </div>
        </div>
      </template>

      <el-skeleton v-if="loading" :rows="6" animated />

      <el-table v-else :data="pagedBooks" stripe>
        <el-table-column prop="title" label="Title" min-width="180" show-overflow-tooltip />
        <el-table-column prop="author" label="Author" min-width="130" />
        <el-table-column prop="publisher" label="Publisher" min-width="140" show-overflow-tooltip />
        <el-table-column prop="category" label="Category" width="120" />
        <el-table-column prop="isbn" label="ISBN" min-width="160" />
        <el-table-column label="Price" width="100">
          <template #default="{ row }">
            {{ row.price != null ? `$${Number(row.price).toFixed(2)}` : 'N/A' }}
          </template>
        </el-table-column>
        <el-table-column label="Stock" width="100">
          <template #default="{ row }">
            <el-tag :type="row.stock > 0 ? 'success' : 'danger'">{{ row.stock }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">Detail</el-button>
            <el-button
              v-if="userStore.isStudent"
              link
              type="success"
              :disabled="row.stock <= 0 || activeBorrowBookIds.has(row.bookId)"
              :loading="borrowingId === row.bookId"
              @click="handleBorrow(row.bookId)"
            >
              {{ activeBorrowBookIds.has(row.bookId) ? 'Borrowed' : 'Borrow' }}
            </el-button>
            <el-button v-if="userStore.isAdmin" link type="warning" @click="openEdit(row)">Edit</el-button>
            <el-popconfirm
              v-if="userStore.isAdmin"
              title="Delete this book?"
              @confirm="handleDelete(row.bookId)"
            >
              <template #reference>
                <el-button link type="danger">Delete</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <div class="footer-row">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="books.length"
          layout="total, sizes, prev, pager, next"
        />

        <el-button v-if="userStore.isAdmin" type="success" @click="openCreate">New Book</el-button>
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="Book Details" width="560px">
      <el-descriptions v-if="selectedBook" :column="1" border>
        <el-descriptions-item label="Title">{{ selectedBook.title }}</el-descriptions-item>
        <el-descriptions-item label="Author">{{ selectedBook.author }}</el-descriptions-item>
        <el-descriptions-item label="Publisher">{{ selectedBook.publisher }}</el-descriptions-item>
        <el-descriptions-item label="ISBN">{{ selectedBook.isbn }}</el-descriptions-item>
        <el-descriptions-item label="Category">{{ selectedBook.category || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Price">{{ selectedBook.price != null ? `$${Number(selectedBook.price).toFixed(2)}` : '-' }}</el-descriptions-item>
        <el-descriptions-item label="Stock">{{ selectedBook.stock }}</el-descriptions-item>
        <el-descriptions-item label="Publish Date">{{ selectedBook.publishDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="Description">{{ selectedBook.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="editVisible" :title="editingId ? 'Edit Book' : 'New Book'" width="620px">
      <el-form :model="form" label-width="88px">
        <el-form-item label="Title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="Author">
          <el-input v-model="form.author" />
        </el-form-item>
        <el-form-item label="Publisher">
          <el-input v-model="form.publisher" />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="form.isbn" />
        </el-form-item>
        <el-form-item label="Category">
          <el-input v-model="form.category" />
        </el-form-item>
        <el-form-item label="Price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="Stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="Publish Date">
          <el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">Save</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createBook,
  deleteBook,
  getAllBooks,
  getBooksByCategory,
  getCategories,
  searchBooks,
  updateBook
} from '@/api/book'
import { borrowBook, getMyBorrowRecords } from '@/api/borrow'
import { useBookStore } from '@/stores/bookStore'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const bookStore = useBookStore()

const books = ref([])
const categories = ref([])
const keyword = ref('')
const selectedCategory = ref('')
const loading = ref(false)
const saving = ref(false)
const borrowingId = ref(null)
const myBorrowRecords = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const editVisible = ref(false)
const selectedBook = ref(null)
const editingId = ref(null)

const form = ref({
  title: '',
  author: '',
  publisher: '',
  isbn: '',
  category: '',
  price: 0,
  stock: 0,
  publishDate: null,
  description: ''
})

const pagedBooks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return books.value.slice(start, start + pageSize.value)
})

const activeBorrowBookIds = computed(() => {
  return new Set(
    myBorrowRecords.value
      .filter((record) => record.status === 'BORROWING')
      .map((record) => record.bookId)
  )
})

let searchTimer = null

const loadBooks = async () => {
  loading.value = true
  try {
    const response = await getAllBooks()
    if (response.code === 200) {
      books.value = response.data || []
      bookStore.setBooks(books.value)
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load books')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await getCategories()
    if (response.code === 200) {
      categories.value = response.data || []
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load categories')
  }
}

const loadMyBorrowRecords = async () => {
  if (!userStore.isStudent) {
    myBorrowRecords.value = []
    return
  }
  try {
    const response = await getMyBorrowRecords()
    if (response.code === 200) {
      myBorrowRecords.value = response.data || []
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load borrow records')
  }
}

const handleSearch = async () => {
  loading.value = true
  try {
    const response = selectedCategory.value
      ? await getBooksByCategory(selectedCategory.value)
      : keyword.value
        ? await searchBooks(keyword.value)
        : await getAllBooks()
    if (response.code === 200) {
      const fetchedBooks = response.data || []
      books.value = selectedCategory.value && keyword.value
        ? fetchedBooks.filter((book) => book.title?.toLowerCase().includes(keyword.value.toLowerCase()))
        : fetchedBooks
      currentPage.value = 1
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Search failed')
  } finally {
    loading.value = false
  }
}

const handleSearchDebounced = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    handleSearch()
  }, 300)
}

const openDetail = (book) => {
  selectedBook.value = book
  detailVisible.value = true
}

const openCreate = () => {
  editingId.value = null
  form.value = {
    title: '',
    author: '',
    publisher: '',
    isbn: '',
    category: '',
    price: 0,
    stock: 0,
    publishDate: null,
    description: ''
  }
  editVisible.value = true
}

const openEdit = (book) => {
  editingId.value = book.bookId
  form.value = { ...book }
  editVisible.value = true
}

const handleSave = async () => {
  if (!form.value.title || !form.value.author || !form.value.publisher || !form.value.isbn) {
    ElMessage.error('Please fill in required fields')
    return
  }

  saving.value = true
  try {
    const payload = {
      ...form.value,
      price: form.value.price === '' ? 0 : form.value.price,
      stock: form.value.stock === '' ? 0 : form.value.stock
    }
    const response = editingId.value
      ? await updateBook(editingId.value, payload)
      : await createBook(payload)

    if (response.code === 200 || response.code === 201) {
      ElMessage.success(editingId.value ? 'Updated successfully' : 'Created successfully')
      editVisible.value = false
      await loadBooks()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Save failed')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (bookId) => {
  try {
    const response = await deleteBook(bookId)
    if (response.code === 200) {
      ElMessage.success('Deleted successfully')
      await loadBooks()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Delete failed')
  }
}

const handleBorrow = async (bookId) => {
  borrowingId.value = bookId
  try {
    const response = await borrowBook(bookId)
    if (response.code === 200) {
      ElMessage.success('Borrowed successfully')
      await Promise.all([loadBooks(), loadMyBorrowRecords()])
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Borrow failed')
  } finally {
    borrowingId.value = null
  }
}

onMounted(async () => {
  await Promise.all([loadBooks(), loadCategories(), loadMyBorrowRecords()])
})
</script>

<style scoped>
.books-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-row,
.footer-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.title {
  font-size: 18px;
  font-weight: 700;
}

.filters {
  display: grid;
  grid-template-columns: minmax(180px, 320px) minmax(160px, 220px);
  gap: 12px;
  max-width: 560px;
  width: 100%;
}

.footer-row {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .header-row,
  .footer-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filters {
    grid-template-columns: 1fr;
    width: 100%;
  }
}
</style>
