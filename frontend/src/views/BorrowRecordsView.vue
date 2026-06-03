<template>
  <div class="borrow-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <div>
            <div class="title">My Borrow Records</div>
            <div class="subtitle">Track active loans, due dates and returned books.</div>
          </div>
          <el-button type="primary" :loading="loading" @click="loadRecords">Refresh</el-button>
        </div>
      </template>

      <el-row :gutter="16" class="summary-row">
        <el-col :xs="24" :sm="8">
          <div class="summary-card active">
            <div class="summary-value">{{ activeCount }}</div>
            <div class="summary-label">Borrowing</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="summary-card returned">
            <div class="summary-value">{{ returnedCount }}</div>
            <div class="summary-label">Returned</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="summary-card due">
            <div class="summary-value">{{ dueSoonCount }}</div>
            <div class="summary-label">Due Soon</div>
          </div>
        </el-col>
      </el-row>

      <el-skeleton v-if="loading" :rows="6" animated />

      <el-empty v-else-if="records.length === 0" description="No borrow records yet">
        <el-button type="primary" @click="router.push('/books')">Find Books</el-button>
      </el-empty>

      <el-table v-else :data="records" stripe>
        <el-table-column prop="bookTitle" label="Book" min-width="180" show-overflow-tooltip />
        <el-table-column prop="bookAuthor" label="Author" min-width="130" />
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
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyBorrowRecords, returnBook } from '@/api/borrow'

const router = useRouter()

const records = ref([])
const loading = ref(false)
const returningId = ref(null)

const activeCount = computed(() => records.value.filter((item) => item.status === 'BORROWING').length)
const returnedCount = computed(() => records.value.filter((item) => item.status === 'RETURNED').length)
const dueSoonCount = computed(() => {
  const now = new Date()
  const sevenDaysLater = new Date()
  sevenDaysLater.setDate(now.getDate() + 7)
  return records.value.filter((item) => {
    if (item.status !== 'BORROWING' || !item.dueDate) {
      return false
    }
    const dueDate = new Date(item.dueDate)
    return dueDate >= now && dueDate <= sevenDaysLater
  }).length
})

const loadRecords = async () => {
  loading.value = true
  try {
    const response = await getMyBorrowRecords()
    if (response.code === 200) {
      records.value = response.data || []
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Failed to load borrow records')
  } finally {
    loading.value = false
  }
}

const handleReturn = async (recordId) => {
  returningId.value = recordId
  try {
    const response = await returnBook(recordId)
    if (response.code === 200) {
      ElMessage.success('Returned successfully')
      await loadRecords()
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || error.message || 'Return failed')
  } finally {
    returningId.value = null
  }
}

onMounted(loadRecords)
</script>

<style scoped>
.borrow-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.title {
  font-size: 18px;
  font-weight: 700;
}

.subtitle {
  margin-top: 4px;
  color: #909399;
  font-size: 13px;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  padding: 18px;
  border-radius: 10px;
  text-align: center;
  background: #f5f7fa;
}

.summary-card.active {
  background: #fff7e8;
}

.summary-card.returned {
  background: #effaf3;
}

.summary-card.due {
  background: #eef5ff;
}

.summary-value {
  font-size: 28px;
  font-weight: 800;
  color: #303133;
}

.summary-label {
  margin-top: 6px;
  color: #606266;
}

.muted {
  color: #909399;
}

@media (max-width: 768px) {
  .header-row {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
