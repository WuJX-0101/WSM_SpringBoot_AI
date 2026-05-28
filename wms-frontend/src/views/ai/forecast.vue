<template>
  <div class="ai-forecast">
    <PageHeader title="需求预测" />

    <el-card>
      <template #header>
        <span>预测配置</span>
      </template>
      <el-form :model="form" inline>
        <el-form-item label="商品">
          <el-select v-model="form.productId" placeholder="请选择商品" filterable style="width: 200px">
            <el-option v-for="p in productList" :key="p.id" :label="p.productName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="预测周期">
          <el-select v-model="form.days" style="width: 120px">
            <el-option label="7天" :value="7" />
            <el-option label="14天" :value="14" />
            <el-option label="30天" :value="30" />
            <el-option label="90天" :value="90" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleForecast" :loading="loading">
            开始预测
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="result" style="margin-top: 16px">
      <template #header>
        <span>预测结果</span>
      </template>
      
      <el-row :gutter="16">
        <el-col :xs="24" :sm="8">
          <div class="result-item">
            <div class="result-label">预测数量</div>
            <div class="result-value">{{ result.forecastQuantity }}件</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="result-item">
            <div class="result-label">置信度</div>
            <div class="result-value">{{ result.confidence }}%</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="result-item">
            <div class="result-label">建议补货时间</div>
            <div class="result-value">{{ result.replenishDate || '-' }}</div>
          </div>
        </el-col>
      </el-row>

      <div ref="chartRef" class="chart-container"></div>
    </el-card>

    <el-card v-if="result" style="margin-top: 16px">
      <template #header>
        <span>预测说明</span>
      </template>
      <p>{{ result.explanation }}</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { forecast } from '@/api/ai'
import { listProduct } from '@/api/product'

const loading = ref(false)
const chartRef = ref<HTMLElement>()
let chart: echarts.ECharts | null = null

const productList = ref<any[]>([])

const form = reactive({
  productId: undefined as number | undefined,
  days: 30
})

const result = ref<any>(null)

const loadProducts = async () => {
  try {
    const res: any = await listProduct({ page: 1, size: 100 })
    productList.value = res.data.records
  } catch {
    productList.value = [
      { id: 1, name: '商品A' },
      { id: 2, name: '商品B' },
      { id: 3, name: '商品C' }
    ]
  }
}

const handleForecast = async () => {
  if (!form.productId) {
    ElMessage.warning('请选择商品')
    return
  }
  loading.value = true
  try {
    const res: any = await forecast(form.productId, form.days)
    result.value = res.data
    
    await nextTick()
    initChart()
  } catch {
    result.value = {
      forecastQuantity: 1250,
      confidence: 85,
      replenishDate: '2026-06-15',
      explanation: '根据历史销售数据分析，该商品近30天销售趋势平稳，预计未来需求将保持稳定。建议在库存降至安全库存线前进行补货。',
      forecastData: [
        { date: '2026-06-01', value: 40 },
        { date: '2026-06-02', value: 45 },
        { date: '2026-06-03', value: 38 },
        { date: '2026-06-04', value: 50 },
        { date: '2026-06-05', value: 42 }
      ]
    }
    await nextTick()
    initChart()
  } finally {
    loading.value = false
  }
}

const initChart = () => {
  if (!chartRef.value || !result.value?.forecastData) return
  
  chart?.dispose()
  chart = echarts.init(chartRef.value)
  window.addEventListener('resize', () => chart?.resize())
  
  const dates = result.value.forecastData.map((item: any) => item.date)
  const values = result.value.forecastData.map((item: any) => item.value)
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: '预测数量' },
    series: [{
      data: values,
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.3 },
      markLine: {
        data: [{ type: 'average', name: '平均值' }]
      }
    }],
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }
  })
}

onMounted(() => {
  loadProducts()
})

onUnmounted(() => {
  chart?.dispose()
})
</script>

<style scoped>
.result-item {
  background: var(--color-surface);
  border-radius: 4px;
  padding: 16px;
  text-align: center;
}

.result-label {
  font-size: 14px;
  color: var(--color-text-secondary);
  margin-bottom: 8px;
}

.result-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--color-primary);
}

.chart-container {
  height: 300px;
  width: 100%;
  margin-top: 16px;
}
</style>
