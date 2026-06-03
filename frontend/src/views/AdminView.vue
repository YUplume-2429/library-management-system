<template>
  <div class="admin-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <div class="title">Admin Panel</div>
          <el-alert
            title="Admin only"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
      </template>

      <el-tabs>
        <el-tab-pane label="Books">
          <div class="toolbar">
            <div class="toolbar-group">
              <el-button type="success" @click="openCreate">New Book</el-button>
              <el-button @click="loadBooks">Refresh</el-button>
              <el-button type="warning" plain @click="openImportPicker">Import CSV</el-button>
            </div>
            <div class="toolbar-group">
              <el-button type="info" plain @click="downloadImportTemplate">Download Template</el-button>
              <el-button type="primary" plain @click="exportBooks">Export Books CSV</el-button>
            </div>
          </div>
          <input
            ref="importInput"
            class="hidden-file-input"
            type="file"
            accept=".csv,text/csv"
            @change="handleImportFileChange"
          />

          <el-skeleton v-if="loading" :rows="6" animated />

          <el-table v-else :data="pagedBooks" stripe>
            <el-table-column prop="title" label="Title" min-width="180" />
            <el-table-column prop="author" label="Author" min-width="120" />
            <el-table-column prop="publisher" label="Publisher" min-width="140" />
            <el-table-column prop="category" label="Category" width="120" />
            <el-table-column prop="isbn" label="ISBN" min-width="150" />
            <el-table-column prop="stock" label="Stock" width="90" />
            <el-table-column label="Actions" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="warning" @click="openEdit(row)">Edit</el-button>
                <el-popconfirm title="Delete this book?" @confirm="handleDelete(row.bookId)">
                  <template #reference>
                    <el-button link type="danger">Delete</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-row">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="allBooks.length"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next"
            />
          </div>
        </el-tab-pane>

        <el-tab-pane label="Users">
          <div class="toolbar">
            <div class="toolbar-group">
              <el-button type="success" @click="openCreateUserForm">Create User</el-button>
              <el-button @click="loadUsers">Refresh Users</el-button>
              <el-button type="warning" plain @click="openUserImportPicker">Import Users CSV</el-button>
            </div>
            <div class="toolbar-group">
              <el-button type="info" plain @click="downloadUserImportTemplate">Download User Template</el-button>
              <el-button type="primary" plain @click="exportUsers">Export Users CSV</el-button>
            </div>
          </div>
          <div class="filter-row">
            <el-input
              v-model="userFilters.keyword"
              placeholder="Search username or name"
              clearable
              @keyup.enter="loadUsers"
              @clear="loadUsers"
            />
            <el-select
              v-model="userFilters.roleName"
              placeholder="All roles"
              clearable
              @change="loadUsers"
              @clear="loadUsers"
            >
              <el-option
                v-for="role in roleOptions"
                :key="role.value"
                :label="role.label"
                :value="role.value"
              />
            </el-select>
            <el-button type="primary" @click="loadUsers">Search</el-button>
            <el-button @click="resetUserFilters">Clear</el-button>
          </div>
          <input
            ref="userImportInput"
            class="hidden-file-input"
            type="file"
            accept=".csv,text/csv"
            @change="handleUserImportFileChange"
          />

          <el-card v-if="userCreateVisible" class="user-create-card">
            <template #header>
              <div class="section-header">
                <div class="section-title">Create User</div>
                <el-button link type="info" @click="closeCreateUserForm">Close</el-button>
              </div>
            </template>
            <el-form :model="userForm" label-width="92px" class="user-form">
              <el-form-item label="Username">
                <el-input v-model="userForm.username" placeholder="Login username" />
              </el-form-item>
              <el-form-item label="Password">
                <el-input v-model="userForm.password" type="password" show-password placeholder="Initial password" />
              </el-form-item>
              <el-form-item label="Name">
                <el-input v-model="userForm.name" placeholder="Display name" />
              </el-form-item>
              <el-form-item label="Email">
                <el-input v-model="userForm.email" placeholder="Optional email" />
              </el-form-item>
              <el-form-item label="Role">
                <el-select v-model="userForm.roleName" placeholder="Select role">
                  <el-option
                    v-for="role in roleOptions"
                    :key="role.value"
                    :label="role.label"
                    :value="role.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="Active">
                <el-switch v-model="userForm.isActive" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="creatingUser" @click="handleCreateUser">Create User</el-button>
                <el-button @click="resetUserForm">Reset</el-button>
                <el-button @click="closeCreateUserForm">Cancel</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <el-skeleton v-if="userLoading" :rows="6" animated />

          <el-table v-else class="users-table" :data="users" stripe>
            <el-table-column prop="username" label="Username" min-width="130" />
            <el-table-column prop="name" label="Name" min-width="130" />
            <el-table-column prop="email" label="Email" min-width="180">
              <template #default="{ row }">
                {{ row.email || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="Role" width="150">
              <template #default="{ row }">
                <el-tag :type="row.roleName === 'ROLE_ADMIN' ? 'danger' : 'success'">
                  {{ row.roleName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="Status" width="120">
              <template #default="{ row }">
                <el-tag :type="row.isActive ? 'success' : 'info'">
                  {{ row.isActive ? 'Active' : 'Disabled' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="Created At" min-width="160" />
            <el-table-column label="Actions" width="220" fixed="right">
              <template #default="{ row }">
                <el-popconfirm
                  v-if="row.roleName === 'ROLE_STUDENT'"
                  title="Reset this student's password to 123456?"
                  @confirm="handleResetStudentPassword(row)"
                >
                  <template #reference>
                    <el-button link type="warning">Reset Password</el-button>
                  </template>
                </el-popconfirm>
                <el-popconfirm
                  title="Delete this user?"
                  :disabled="row.userId === userStore.user?.userId"
                  @confirm="handleDeleteUser(row)"
                >
                  <template #reference>
                    <el-button
                      link
                      type="danger"
                      :disabled="row.userId === userStore.user?.userId"
                    >
                      Delete
                    </el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="Stats">
          <el-row :gutter="16">
            <el-col :xs="24" :sm="12" :md="6">
              <div class="stat-card">
                <div class="stat-value">{{ totalBooks }}</div>
                <div class="stat-label">Total Books</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="stat-card">
                <div class="stat-value">{{ totalStock }}</div>
                <div class="stat-label">Total Stock</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="stat-card">
                <div class="stat-value">{{ availableCount }}</div>
                <div class="stat-label">Available Books</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="stat-card">
                <div class="stat-value">{{ outOfStockCount }}</div>
                <div class="stat-label">Out of Stock</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="6">
              <div class="stat-card">
                <div class="stat-value">{{ activeBorrowCount }}</div>
                <div class="stat-label">Active Borrows</div>
              </div>
            </el-col>
          </el-row>

          <el-card class="category-card">
            <template #header>
              <div class="section-title">Category Distribution</div>
            </template>
            <el-empty v-if="categoryStats.length === 0" description="No category data" />
            <div v-else class="category-list">
              <div v-for="item in categoryStats" :key="item.category" class="category-item">
                <div class="category-meta">
                  <span>{{ item.category }}</span>
                  <strong>{{ item.count }}</strong>
                </div>
                <el-progress :percentage="item.percentage" :stroke-width="10" />
              </div>
            </div>
          </el-card>
        </el-tab-pane>

        <el-tab-pane label="Borrow Records">
          <div class="toolbar">
            <el-button @click="loadBorrowRecords">Refresh Records</el-button>
            <el-button type="primary" plain @click="exportBorrowRecords">Export Records CSV</el-button>
          </div>

          <el-row :gutter="16" class="borrow-summary-row">
            <el-col :xs="24" :sm="12" :md="5">
              <div class="stat-card">
                <div class="stat-value">{{ borrowRecordCount }}</div>
                <div class="stat-label">Borrow Records</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="5">
              <div class="stat-card">
                <div class="stat-value">{{ currentBorrowingCount }}</div>
                <div class="stat-label">Borrowing Now</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="5">
              <div class="stat-card">
                <div class="stat-value">{{ returnedBorrowCount }}</div>
                <div class="stat-label">Returned</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="5">
              <div class="stat-card">
                <div class="stat-value">{{ studentBorrowStats.length }}</div>
                <div class="stat-label">Students With Records</div>
              </div>
            </el-col>
            <el-col :xs="24" :sm="12" :md="4">
              <div class="stat-card">
                <div class="stat-value">{{ bookBorrowStats.length }}</div>
                <div class="stat-label">Borrowed Books</div>
              </div>
            </el-col>
          </el-row>

          <el-row :gutter="16" class="borrow-stat-row">
            <el-col :xs="24" :lg="12">
              <el-card>
                <template #header>
                  <div class="section-header">
                    <div class="section-title">Student Borrow Statistics</div>
                    <el-button link type="primary" @click="openBorrowStatsDialog('student')">More</el-button>
                  </div>
                </template>
                <el-table :data="topStudentBorrowStats" stripe max-height="320">
                  <el-table-column prop="username" label="Username" min-width="120" />
                  <el-table-column prop="totalCount" label="Total" width="80" />
                  <el-table-column prop="activeCount" label="Borrowing" width="100" />
                  <el-table-column prop="distinctBookCount" label="Books" width="80" />
                  <el-table-column label="Details" width="100">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="openBorrowDetail('student', row)">Detail</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-col>

            <el-col :xs="24" :lg="12">
              <el-card>
                <template #header>
                  <div class="section-header">
                    <div class="section-title">Book Borrow Statistics</div>
                    <el-button link type="primary" @click="openBorrowStatsDialog('book')">More</el-button>
                  </div>
                </template>
                <el-table :data="topBookBorrowStats" stripe max-height="320">
                  <el-table-column prop="bookTitle" label="Book" min-width="160" show-overflow-tooltip />
                  <el-table-column prop="bookAuthor" label="Author" min-width="120" />
                  <el-table-column prop="totalCount" label="Total" width="80" />
                  <el-table-column prop="activeCount" label="Borrowing" width="100" />
                  <el-table-column label="Details" width="100">
                    <template #default="{ row }">
                      <el-button link type="primary" @click="openBorrowDetail('book', row)">Detail</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-card>
            </el-col>
          </el-row>

          <el-skeleton v-if="borrowLoading" :rows="6" animated />

          <el-table v-else :data="borrowRecords" stripe>
            <el-table-column prop="bookTitle" label="Book" min-width="180" show-overflow-tooltip />
            <el-table-column label="Username" min-width="120">
              <template #default="{ row }">
                {{ row.username || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="borrowDate" label="Borrow Date" min-width="160" />
            <el-table-column prop="dueDate" label="Due Date" width="120" />
            <el-table-column prop="returnDate" label="Return Date" min-width="160">
              <template #default="{ row }">
                {{ row.returnDate || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="Status" width="120">
              <template #default="{ row }">
                <el-tag :type="row.status === 'BORROWING' ? 'warning' : 'success'">
                  {{ row.status === 'BORROWING' ? 'Borrowing' : 'Returned' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="Actions" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  v-if="row.status === 'BORROWING'"
                  link
                  type="primary"
                  :loading="returningId === row.recordId"
                  @click="handleReturn(row.recordId)"
                >
                  Return
                </el-button>
                <span v-else class="muted">Done</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

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

    <el-dialog v-model="importVisible" title="Import Books CSV" width="860px">
      <el-alert
        title="Required columns: title, author, publisher, isbn. Optional columns: category, price, stock, publishDate, description."
        type="info"
        :closable="false"
        show-icon
      />

      <el-table class="import-preview" :data="importPreview" max-height="360" stripe>
        <el-table-column prop="title" label="Title" min-width="160" />
        <el-table-column prop="author" label="Author" min-width="120" />
        <el-table-column prop="publisher" label="Publisher" min-width="140" />
        <el-table-column prop="isbn" label="ISBN" min-width="150" />
        <el-table-column prop="category" label="Category" width="120" />
        <el-table-column prop="price" label="Price" width="90" />
        <el-table-column prop="stock" label="Stock" width="90" />
      </el-table>

      <template #footer>
        <el-button @click="importVisible = false">Cancel</el-button>
        <el-button type="primary" :loading="importing" :disabled="importPreview.length === 0" @click="confirmImport">
          Import {{ importPreview.length }} Books
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="userImportVisible" title="Import Users CSV" width="860px">
      <el-alert
        title="Required columns: username, password, name, roleName. Optional columns: email, isActive."
        type="info"
        :closable="false"
        show-icon
      />

      <el-table class="import-preview" :data="userImportPreview" max-height="360" stripe>
        <el-table-column prop="username" label="Username" min-width="130" />
        <el-table-column prop="name" label="Name" min-width="130" />
        <el-table-column prop="email" label="Email" min-width="180" />
        <el-table-column prop="roleName" label="Role" min-width="150" />
        <el-table-column label="Active" width="100">
          <template #default="{ row }">
            {{ row.isActive ? 'Yes' : 'No' }}
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="userImportVisible = false">Cancel</el-button>
        <el-button
          type="primary"
          :loading="importingUsers"
          :disabled="userImportPreview.length === 0"
          @click="confirmUserImport"
        >
          Import {{ userImportPreview.length }} Users
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="borrowStatsVisible" :title="borrowStatsTitle" width="920px">
      <el-table v-if="borrowStatsType === 'student'" :data="studentBorrowStats" stripe max-height="460">
        <el-table-column prop="username" label="Username" min-width="120" />
        <el-table-column prop="totalCount" label="Total" width="90" />
        <el-table-column prop="activeCount" label="Borrowing" width="110" />
        <el-table-column prop="distinctBookCount" label="Books" width="90" />
        <el-table-column label="Details" width="110" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openBorrowDetail('student', row)">Detail</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-table v-else :data="bookBorrowStats" stripe max-height="460">
        <el-table-column prop="bookTitle" label="Book" min-width="180" show-overflow-tooltip />
        <el-table-column prop="bookAuthor" label="Author" min-width="140" />
        <el-table-column prop="totalCount" label="Total" width="90" />
        <el-table-column prop="activeCount" label="Borrowing" width="110" />
        <el-table-column label="Details" width="110" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openBorrowDetail('book', row)">Detail</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="borrowDetailVisible" :title="borrowDetailTitle" width="920px">
      <el-table :data="borrowDetailRecords" stripe max-height="420">
        <el-table-column prop="username" label="Username" min-width="120" />
        <el-table-column prop="bookTitle" label="Book" min-width="180" show-overflow-tooltip />
        <el-table-column prop="bookAuthor" label="Author" min-width="120" />
        <el-table-column prop="borrowDate" label="Borrow Date" min-width="160" />
        <el-table-column prop="dueDate" label="Due Date" width="120" />
        <el-table-column prop="returnDate" label="Return Date" min-width="160">
          <template #default="{ row }">
            {{ row.returnDate || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="Status" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'BORROWING' ? 'warning' : 'success'">
              {{ row.status === 'BORROWING' ? 'Borrowing' : 'Returned' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createBook, deleteBook, getAllBooks, importBooks, updateBook } from '@/api/book'
import { getAllBorrowRecords, returnBook } from '@/api/borrow'
import { createUser, deleteUser, getAllUsers, importUsers, resetStudentPassword } from '@/api/user'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const allBooks = ref([])
const borrowRecords = ref([])
const users = ref([])
const loading = ref(false)
const borrowLoading = ref(false)
const userLoading = ref(false)
const saving = ref(false)
const importing = ref(false)
const importingUsers = ref(false)
const creatingUser = ref(false)
const returningId = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const editVisible = ref(false)
const importVisible = ref(false)
const userImportVisible = ref(false)
const userCreateVisible = ref(false)
const borrowStatsVisible = ref(false)
const borrowDetailVisible = ref(false)
const borrowStatsType = ref('student')
const editingId = ref(null)
const importInput = ref(null)
const userImportInput = ref(null)
const importPreview = ref([])
const userImportPreview = ref([])
const borrowDetailTitle = ref('')
const borrowDetailRecords = ref([])

const roleOptions = [
  { label: 'Administrator', value: 'ROLE_ADMIN' },
  { label: 'Student', value: 'ROLE_STUDENT' }
]

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

const userForm = ref({
  username: '',
  password: '',
  name: '',
  email: '',
  roleName: 'ROLE_STUDENT',
  isActive: true
})

const userFilters = ref({
  keyword: '',
  roleName: ''
})

const pagedBooks = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return allBooks.value.slice(start, start + pageSize.value)
})

const totalBooks = computed(() => allBooks.value.length)
const totalStock = computed(() => allBooks.value.reduce((sum, book) => sum + (Number(book.stock) || 0), 0))
const availableCount = computed(() => allBooks.value.filter((book) => (Number(book.stock) || 0) > 0).length)
const outOfStockCount = computed(() => allBooks.value.filter((book) => (Number(book.stock) || 0) === 0).length)
const activeBorrowCount = computed(() => borrowRecords.value.filter((record) => record.status === 'BORROWING').length)
const borrowRecordCount = computed(() => borrowRecords.value.length)
const currentBorrowingCount = computed(() => borrowRecords.value.filter((record) => record.status === 'BORROWING').length)
const returnedBorrowCount = computed(() => borrowRecords.value.filter((record) => record.status === 'RETURNED').length)
const borrowStatsTitle = computed(() =>
  borrowStatsType.value === 'student' ? 'All Student Borrow Statistics' : 'All Book Borrow Statistics'
)
const categoryStats = computed(() => {
  const counts = allBooks.value.reduce((result, book) => {
    const category = book.category || 'Uncategorized'
    result[category] = (result[category] || 0) + 1
    return result
  }, {})

  return Object.entries(counts)
    .map(([category, count]) => ({
      category,
      count,
      percentage: totalBooks.value ? Math.round((count / totalBooks.value) * 100) : 0
    }))
    .sort((left, right) => right.count - left.count)
})

const studentBorrowStats = computed(() => {
  const stats = new Map()
  for (const record of borrowRecords.value) {
    if (!record.userId) {
      continue
    }
    if (!stats.has(record.userId)) {
      stats.set(record.userId, {
        userId: record.userId,
        username: record.username || '-',
        totalCount: 0,
        activeCount: 0,
        bookIds: new Set()
      })
    }
    const stat = stats.get(record.userId)
    stat.totalCount += 1
    if (record.status === 'BORROWING') {
      stat.activeCount += 1
    }
    if (record.bookId) {
      stat.bookIds.add(record.bookId)
    }
  }

  return Array.from(stats.values())
    .map((stat) => ({
      ...stat,
      distinctBookCount: stat.bookIds.size
    }))
    .sort((left, right) => right.totalCount - left.totalCount)
})

const bookBorrowStats = computed(() => {
  const stats = new Map()
  for (const record of borrowRecords.value) {
    if (!record.bookId) {
      continue
    }
    if (!stats.has(record.bookId)) {
      stats.set(record.bookId, {
        bookId: record.bookId,
        bookTitle: record.bookTitle || '-',
        bookAuthor: record.bookAuthor || '-',
        totalCount: 0,
        activeCount: 0
      })
    }
    const stat = stats.get(record.bookId)
    stat.totalCount += 1
    if (record.status === 'BORROWING') {
      stat.activeCount += 1
    }
  }

  return Array.from(stats.values()).sort((left, right) => right.totalCount - left.totalCount)
})

const topStudentBorrowStats = computed(() => studentBorrowStats.value.slice(0, 5))
const topBookBorrowStats = computed(() => bookBorrowStats.value.slice(0, 5))

const loadBooks = async () => {
  loading.value = true
  try {
    const response = await getAllBooks()
    if (response.code === 200) {
      allBooks.value = response.data || []
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load books')
  } finally {
    loading.value = false
  }
}

const loadBorrowRecords = async () => {
  borrowLoading.value = true
  try {
    const response = await getAllBorrowRecords()
    if (response.code === 200) {
      borrowRecords.value = response.data || []
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load borrow records')
  } finally {
    borrowLoading.value = false
  }
}

const loadUsers = async () => {
  userLoading.value = true
  try {
    const response = await getAllUsers({
      keyword: userFilters.value.keyword || undefined,
      roleName: userFilters.value.roleName || undefined
    })
    if (response.code === 200) {
      users.value = response.data || []
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load users')
  } finally {
    userLoading.value = false
  }
}

const resetUserFilters = async () => {
  userFilters.value = {
    keyword: '',
    roleName: ''
  }
  await loadUsers()
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

const handleReturn = async (recordId) => {
  returningId.value = recordId
  try {
    const response = await returnBook(recordId)
    if (response.code === 200) {
      ElMessage.success('Returned successfully')
      await Promise.all([loadBooks(), loadBorrowRecords()])
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Return failed')
  } finally {
    returningId.value = null
  }
}

const resetUserForm = () => {
  userForm.value = {
    username: '',
    password: '',
    name: '',
    email: '',
    roleName: 'ROLE_STUDENT',
    isActive: true
  }
}

const openCreateUserForm = () => {
  userCreateVisible.value = true
}

const closeCreateUserForm = () => {
  resetUserForm()
  userCreateVisible.value = false
}

const handleCreateUser = async () => {
  if (!userForm.value.username || !userForm.value.password || !userForm.value.name || !userForm.value.roleName) {
    ElMessage.error('Please fill in username, password, name and role')
    return
  }

  creatingUser.value = true
  try {
    const response = await createUser({ ...userForm.value })
    if (response.code === 200 || response.code === 201) {
      ElMessage.success('User created successfully')
      resetUserForm()
      userCreateVisible.value = false
      await loadUsers()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Create user failed')
  } finally {
    creatingUser.value = false
  }
}

const handleDeleteUser = async (user) => {
  try {
    const response = await deleteUser(user.userId)
    if (response.code === 200) {
      ElMessage.success('User deleted successfully')
      await Promise.all([loadUsers(), loadBorrowRecords(), loadBooks()])
    }
  } catch (error) {
    if (error.response?.status === 409 || error.response?.data?.code === 409) {
      await confirmForceDeleteUser(user, error.response?.data?.message)
      return
    }
    ElMessage.error(error.response?.data?.message || error.message || 'Delete user failed')
  }
}

const handleResetStudentPassword = async (user) => {
  try {
    const response = await resetStudentPassword(user.userId)
    if (response.code === 200) {
      ElMessage.success(`${user.username}'s password has been reset to 123456`)
      await loadUsers()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Reset password failed')
  }
}

const openBorrowStatsDialog = (type) => {
  borrowStatsType.value = type
  borrowStatsVisible.value = true
}

const openBorrowDetail = (type, stat) => {
  if (type === 'student') {
    borrowDetailTitle.value = `${stat.username} Borrow Records`
    borrowDetailRecords.value = borrowRecords.value.filter((record) => record.userId === stat.userId)
  } else {
    borrowDetailTitle.value = `${stat.bookTitle} Borrow Records`
    borrowDetailRecords.value = borrowRecords.value.filter((record) => record.bookId === stat.bookId)
  }
  borrowDetailVisible.value = true
}

const confirmForceDeleteUser = async (user, message) => {
  try {
    await ElMessageBox.confirm(
      `${message || 'This user has active borrow records.'} If you continue, all borrowed books will be treated as returned, the related borrow records will be deleted, and the user will be removed. Continue?`,
      'User Has Active Borrow Records',
      {
        confirmButtonText: 'Force Delete',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }
    )

    const response = await deleteUser(user.userId, true)
    if (response.code === 200) {
      ElMessage.success('User force deleted successfully')
      await Promise.all([loadUsers(), loadBorrowRecords(), loadBooks()])
    }
  } catch (error) {
    if (error === 'cancel' || error === 'close') {
      return
    }
    ElMessage.error(error.response?.data?.message || error.message || 'Force delete user failed')
  }
}

const openImportPicker = () => {
  if (importInput.value) {
    importInput.value.value = ''
    importInput.value.click()
  }
}

const parseCsv = (text) => {
  const rows = []
  let current = ''
  let row = []
  let inQuotes = false

  for (let index = 0; index < text.length; index++) {
    const char = text[index]
    const nextChar = text[index + 1]

    if (char === '"' && inQuotes && nextChar === '"') {
      current += '"'
      index++
    } else if (char === '"') {
      inQuotes = !inQuotes
    } else if (char === ',' && !inQuotes) {
      row.push(current)
      current = ''
    } else if ((char === '\n' || char === '\r') && !inQuotes) {
      if (char === '\r' && nextChar === '\n') {
        index++
      }
      row.push(current)
      if (row.some((cell) => cell.trim() !== '')) {
        rows.push(row)
      }
      row = []
      current = ''
    } else {
      current += char
    }
  }

  row.push(current)
  if (row.some((cell) => cell.trim() !== '')) {
    rows.push(row)
  }
  return rows
}

const normalizeHeader = (header) => header.trim().replace(/^\uFEFF/, '').toLowerCase()

const parseNumber = (value, fallback) => {
  if (value == null || value === '') {
    return fallback
  }
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

const buildBooksFromCsv = (text) => {
  const rows = parseCsv(text)
  if (rows.length < 2) {
    throw new Error('CSV must include a header row and at least one data row')
  }

  const headers = rows[0].map(normalizeHeader)
  const requiredHeaders = ['title', 'author', 'publisher', 'isbn']
  const missingHeaders = requiredHeaders.filter((header) => !headers.includes(header))
  if (missingHeaders.length > 0) {
    throw new Error(`Missing required columns: ${missingHeaders.join(', ')}`)
  }

  const valueOf = (row, field) => {
    const index = headers.indexOf(field)
    return index >= 0 ? (row[index] || '').trim() : ''
  }

  return rows.slice(1).map((row, index) => {
    const book = {
      title: valueOf(row, 'title'),
      author: valueOf(row, 'author'),
      publisher: valueOf(row, 'publisher'),
      isbn: valueOf(row, 'isbn'),
      category: valueOf(row, 'category'),
      price: parseNumber(valueOf(row, 'price'), 0),
      stock: parseNumber(valueOf(row, 'stock'), 0),
      publishDate: valueOf(row, 'publishdate') || null,
      description: valueOf(row, 'description')
    }

    if (!book.title || !book.author || !book.publisher || !book.isbn) {
      throw new Error(`Row ${index + 2} is missing title, author, publisher or isbn`)
    }
    if (book.price < 0 || book.stock < 0) {
      throw new Error(`Row ${index + 2} has invalid price or stock`)
    }

    return book
  })
}

const parseBoolean = (value, fallback = true) => {
  if (value == null || value === '') {
    return fallback
  }
  const normalized = String(value).trim().toLowerCase()
  if (['true', '1', 'yes', 'y', 'active'].includes(normalized)) {
    return true
  }
  if (['false', '0', 'no', 'n', 'disabled'].includes(normalized)) {
    return false
  }
  return fallback
}

const buildUsersFromCsv = (text) => {
  const rows = parseCsv(text)
  if (rows.length < 2) {
    throw new Error('CSV must include a header row and at least one data row')
  }

  const headers = rows[0].map(normalizeHeader)
  const requiredHeaders = ['username', 'password', 'name']
  const missingHeaders = requiredHeaders.filter((header) => !headers.includes(header))
  if (missingHeaders.length > 0) {
    throw new Error(`Missing required columns: ${missingHeaders.join(', ')}`)
  }
  if (!headers.includes('rolename') && !headers.includes('role')) {
    throw new Error('Missing required column: roleName')
  }

  const valueOf = (row, field) => {
    const index = headers.indexOf(field)
    return index >= 0 ? (row[index] || '').trim() : ''
  }

  return rows.slice(1).map((row, index) => {
    const roleName = valueOf(row, 'rolename') || valueOf(row, 'role')
    const user = {
      username: valueOf(row, 'username'),
      password: valueOf(row, 'password'),
      name: valueOf(row, 'name'),
      email: valueOf(row, 'email'),
      roleName,
      isActive: parseBoolean(valueOf(row, 'isactive'), true)
    }

    if (!user.username || !user.password || !user.name || !user.roleName) {
      throw new Error(`Row ${index + 2} is missing username, password, name or roleName`)
    }
    if (!['ROLE_ADMIN', 'ROLE_STUDENT'].includes(user.roleName)) {
      throw new Error(`Row ${index + 2} has invalid roleName: ${user.roleName}`)
    }

    return user
  })
}

const handleImportFileChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) {
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    try {
      importPreview.value = buildBooksFromCsv(String(reader.result || ''))
      importVisible.value = true
    } catch (error) {
      importPreview.value = []
      ElMessage.error(error.message || 'Failed to parse CSV')
    }
  }
  reader.onerror = () => {
    ElMessage.error('Failed to read CSV file')
  }
  reader.readAsText(file, 'UTF-8')
}

const openUserImportPicker = () => {
  if (userImportInput.value) {
    userImportInput.value.value = ''
    userImportInput.value.click()
  }
}

const handleUserImportFileChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) {
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    try {
      userImportPreview.value = buildUsersFromCsv(String(reader.result || ''))
      userImportVisible.value = true
    } catch (error) {
      userImportPreview.value = []
      ElMessage.error(error.message || 'Failed to parse CSV')
    }
  }
  reader.onerror = () => {
    ElMessage.error('Failed to read CSV file')
  }
  reader.readAsText(file, 'UTF-8')
}

const confirmImport = async () => {
  importing.value = true
  try {
    const response = await importBooks(importPreview.value)
    if (response.code === 200) {
      ElMessage.success(`Imported ${response.data?.length || importPreview.value.length} books`)
      importVisible.value = false
      importPreview.value = []
      await loadBooks()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Import failed')
  } finally {
    importing.value = false
  }
}

const confirmUserImport = async () => {
  importingUsers.value = true
  try {
    const response = await importUsers(userImportPreview.value)
    if (response.code === 200) {
      ElMessage.success(`Imported ${response.data?.length || userImportPreview.value.length} users`)
      userImportVisible.value = false
      userImportPreview.value = []
      await loadUsers()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Import users failed')
  } finally {
    importingUsers.value = false
  }
}

const escapeCsv = (value) => {
  const text = value == null ? '' : String(value)
  return `"${text.replaceAll('"', '""')}"`
}

const downloadCsv = (filename, headers, rows) => {
  const content = [
    headers.map(escapeCsv).join(','),
    ...rows.map((row) => headers.map((header) => escapeCsv(row[header])).join(','))
  ].join('\n')
  const blob = new Blob([`\uFEFF${content}`], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}

const exportBooks = () => {
  downloadCsv(
    'books.csv',
    ['bookId', 'title', 'author', 'publisher', 'category', 'isbn', 'price', 'stock', 'publishDate'],
    allBooks.value
  )
}

const downloadImportTemplate = () => {
  downloadCsv(
    'book-import-template.csv',
    ['title', 'author', 'publisher', 'isbn', 'category', 'price', 'stock', 'publishDate', 'description'],
    [
      {
        title: 'Example Book',
        author: 'Example Author',
        publisher: 'Example Publisher',
        isbn: '978-0-000-00000-0',
        category: 'Programming',
        price: 59.9,
        stock: 5,
        publishDate: '2026-01-01',
        description: 'Replace this row with your book data'
      }
    ]
  )
}

const downloadUserImportTemplate = () => {
  downloadCsv(
    'user-import-template.csv',
    ['username', 'password', 'name', 'email', 'roleName', 'isActive'],
    [
      {
        username: 'student3',
        password: 'student123',
        name: 'Student Three',
        email: 'student3@library.com',
        roleName: 'ROLE_STUDENT',
        isActive: true
      }
    ]
  )
}

const exportUsers = () => {
  downloadCsv(
    'users.csv',
    ['userId', 'username', 'name', 'email', 'roleName', 'isActive', 'createdAt'],
    users.value
  )
}

const exportBorrowRecords = () => {
  downloadCsv(
    'borrow-records.csv',
    ['recordId', 'username', 'bookTitle', 'borrowDate', 'dueDate', 'returnDate', 'status'],
    borrowRecords.value
  )
}

onMounted(async () => {
  await Promise.all([loadBooks(), loadBorrowRecords(), loadUsers()])
})
</script>

<style scoped>
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-row {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.title {
  font-size: 18px;
  font-weight: 700;
}

.toolbar,
.pagination-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.toolbar-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-row {
  display: grid;
  grid-template-columns: minmax(180px, 280px) minmax(160px, 220px) auto auto;
  gap: 12px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.stat-card {
  padding: 20px;
  border-radius: 8px;
  background: #f5f7fa;
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #409eff;
}

.stat-label {
  margin-top: 8px;
  color: #606266;
}

.muted {
  color: #909399;
}

.category-card {
  margin-top: 16px;
}

.borrow-summary-row,
.borrow-stat-row {
  margin-bottom: 16px;
}

.user-create-card,
.users-table {
  margin-bottom: 16px;
}

.user-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(260px, 1fr));
  column-gap: 16px;
}

.hidden-file-input {
  display: none;
}

.import-preview {
  margin-top: 16px;
}

.category-list {
  display: grid;
  gap: 14px;
}

.category-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 6px;
  color: #303133;
}

@media (max-width: 768px) {
  .toolbar,
  .pagination-row {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-row {
    grid-template-columns: 1fr;
  }

  .user-form {
    grid-template-columns: 1fr;
  }
}
</style>
