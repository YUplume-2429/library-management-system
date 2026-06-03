<template>
  <div class="not-found">
    <el-result icon="warning" title="404" sub-title="Page not found">
      <template #extra>
        <el-button type="primary" @click="goHome">Back to books</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/books')
}
</script>

<style scoped>
.not-found {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
