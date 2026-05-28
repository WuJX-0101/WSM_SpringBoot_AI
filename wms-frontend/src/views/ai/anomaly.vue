<template>
  <div class="ai-anomaly">
    <PageHeader title="异常检测" />

    <el-card>
      <template #header>
        <div class="card-header">
          <span>异常检测结果</span>
          <el-button type="primary" @click="loadData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="anomalyList" v-loading="loading" border>
        <el-table-column prop="productName" label="商品" min-width="150" />
        <el-table-column prop="type" label="异常类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="严重程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTag(row.level)" effect="dark">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="异常描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="detectedTime" label="检测时间" width="170" />
        <el-table-column prop="suggestion" label="处理建议" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { getAnomaly } from '@/api/ai'

const loading = ref(false)
const anomalyList = ref<any[]>([])

const getTypeTag = (type: string) => {
  const map: Record<string, string> = {
    '库存异常': 'danger',
    '订单异常': 'warning',
    '销量异常': 'info'
  }
  return map[type] || 'info'
}

const getTypeText = (type: string) => {
  return type || '未知'
}

const getLevelTag = (level: string) => {
  const map: Record<string, string> = {
    '高': 'danger',
    '中': 'warning',
    '低': 'info'
  }
  return map[level] || 'info'
}

const getLevelText = (level: string) => {
  return level || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getAnomaly()
    anomalyList.value = res.data.anomalies || []
  } catch {
    anomalyList.value = [
      {
        productName: '商品A',
        type: 'stockout',
        level: 'high',
        description: '当前库存为0，已无法满足订单需求',
        detectedTime: '2026-05-28 10:30:00',
        suggestion: '立即联系供应商紧急补货，同时暂停该商品的销售'
      },
      {
        productName: '商品B',
        type: 'overstock',
        level: 'medium',
        description: '库存周转天数超过90天，存在积压风险',
        detectedTime: '2026-05-28 09:15:00',
        suggestion: '建议进行促销活动，减少采购量'
      },
      {
        productName: '商品C',
        type: 'sales_spike',
        level: 'low',
        description: '近7天销量较前期增长200%',
        detectedTime: '2026-05-28 08:45:00',
        suggestion: '关注库存变化，必要时增加安全库存'
      }
    ]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
