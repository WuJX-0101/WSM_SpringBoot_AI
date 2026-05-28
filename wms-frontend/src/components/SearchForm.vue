<template>
  <div class="search-form">
    <el-form :model="model" inline @submit.prevent="handleSearch">
      <slot />
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  model: Record<string, any>
}>()

const emit = defineEmits<{
  search: []
  reset: []
}>()

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
  padding: 16px;
  background: var(--color-surface);
  border-radius: 4px;
}

.search-form :deep(.el-select) {
  min-width: 160px;
}

@media (max-width: 639px) {
  .search-form :deep(.el-form-item) {
    margin-bottom: 12px;
  }
}
</style>
