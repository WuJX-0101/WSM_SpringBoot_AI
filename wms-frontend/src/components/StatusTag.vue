<template>
  <el-tag :type="type" :effect="effect" :size="size">
    <slot>{{ label }}</slot>
  </el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  status: number | string
  statusMap?: Record<number | string, { label: string; type: string }>
  size?: 'large' | 'default' | 'small'
  effect?: 'dark' | 'light' | 'plain'
}>()

const defaultMap: Record<number, { label: string; type: string }> = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已审核', type: 'info' },
  2: { label: '已完成', type: 'success' },
  3: { label: '已取消', type: 'danger' }
}

const statusInfo = computed(() => {
  const map = props.statusMap || defaultMap
  return (map as any)[props.status] || { label: String(props.status), type: 'info' }
})

const type = computed(() => statusInfo.value.type as any)
const label = computed(() => statusInfo.value.label)
</script>
