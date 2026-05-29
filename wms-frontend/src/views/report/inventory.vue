<template>
  <div class="report-inventory">
    <PageHeader title="库存报表" />

    <el-row :gutter="16">
      <el-col :xs="24" :sm="8">
        <div class="stat-card">
          <div class="stat-label">商品种类</div>
          <div class="stat-value">{{ stats.productCount }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="stat-card">
          <div class="stat-label">库存总量</div>
          <div class="stat-value">{{ stats.totalQuantity }}</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="8">
        <div class="stat-card">
          <div class="stat-label">库存总值</div>
          <div class="stat-value">¥{{ stats.totalValue }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <span>仓库库存分布</span>
          </template>
          <div ref="warehouseChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <span>库存数量TOP10</span>
          </template>
          <div ref="topChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <span>库存明细</span>
      </template>
      <el-table :data="inventoryList" v-loading="loading" border>
        <el-table-column prop="productName" label="商品" min-width="150" />
        <el-table-column prop="warehouseName" label="仓库" width="100" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="costPrice" label="单价" width="100">
          <template #default="{ row }">¥{{ row.costPrice }}</template>
        </el-table-column>
        <el-table-column label="金额" width="120">
          <template #default="{ row }">¥{{ (row.quantity * row.costPrice).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import echarts from '@/utils/echarts'
import type { ECharts } from '@/utils/echarts'
import PageHeader from '@/components/PageHeader.vue'
import { listInventory } from '@/api/inventory'
import type { Inventory } from '@/types/api'

const loading = ref(false)

const stats = reactive({
  productCount: 0,
  totalQuantity: 0,
  totalValue: '0'
})

const inventoryList = ref<Inventory[]>([])

const warehouseChartRef = ref<HTMLElement>()
const topChartRef = ref<HTMLElement>()

let warehouseChart: ECharts | null = null
let topChart: ECharts | null = null
const resizeHandlers: Array<() => void> = []

const loadInventory = async () => {
  loading.value = true
  try {
    const res: any = await listInventory({ page: 1, size: 100 })
    inventoryList.value = res.data.records

    stats.productCount = res.data.total
    stats.totalQuantity = res.data.records.reduce((sum: number, item: any) => sum + item.quantity, 0)
    stats.totalValue = res.data.records.reduce((sum: number, item: any) => sum + item.quantity * (item.costPrice || 0), 0).toFixed(2)
  } finally {
    loading.value = false
  }
}

const initWarehouseChart = () => {
  if (!warehouseChartRef.value) return

  warehouseChart = echarts.init(warehouseChartRef.value)
  const handler = () => warehouseChart?.resize()
  window.addEventListener('resize', handler)
  resizeHandlers.push(handler)

  interface WarehouseData {
    name: string
    value: number
  }

  const warehouseData = inventoryList.value.reduce<WarehouseData[]>((acc, item) => {
    const existing = acc.find(w => w.name === item.warehouseName)
    if (existing) {
      existing.value += item.quantity
    } else {
      acc.push({ name: item.warehouseName || '未知仓库', value: item.quantity })
    }
    return acc
  }, [])

  warehouseChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: warehouseData,
      label: { show: true, formatter: '{b}: {c}' }
    }]
  })
}

const initTopChart = () => {
  if (!topChartRef.value) return

  topChart = echarts.init(topChartRef.value)
  const handler = () => topChart?.resize()
  window.addEventListener('resize', handler)
  resizeHandlers.push(handler)

  const sorted = [...inventoryList.value].sort((a, b) => b.quantity - a.quantity).slice(0, 10)
  const names = sorted.map(item => item.productName || '未知商品')
  const values = sorted.map(item => item.quantity)

  topChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: names.reverse() },
    series: [{ data: values.reverse(), type: 'bar' }],
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }
  })
}

onMounted(async () => {
  await loadInventory()
  await nextTick()
  initWarehouseChart()
  initTopChart()
})

onUnmounted(() => {
  warehouseChart?.dispose()
  topChart?.dispose()
  resizeHandlers.forEach(handler => {
    window.removeEventListener('resize', handler)
  })
  resizeHandlers.length = 0
})
</script>

<style scoped>
.stat-card {
  background: var(--color-surface);
  border-radius: 4px;
  padding: 20px;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: var(--color-primary);
}

.chart-container {
  height: 300px;
  width: 100%;
}
</style>
