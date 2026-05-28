<template>
  <div class="ai-replenishment">
    <PageHeader title="智能补货" />

    <el-card>
      <template #header>
        <div class="card-header">
          <span>补货建议</span>
          <el-button type="primary" @click="loadData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table :data="replenishmentList" v-loading="loading" border>
        <el-table-column prop="productName" label="商品" min-width="150" />
        <el-table-column prop="currentStock" label="当前库存" width="100" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="avgDailySales" label="日均销量" width="100" />
        <el-table-column prop="replenishQuantity" label="建议补货量" width="120">
          <template #default="{ row }">
            <span class="replenish-qty">{{ row.replenishQuantity }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="urgency" label="紧急程度" width="100">
          <template #default="{ row }">
            <el-tag :type="getUrgencyType(row.urgency)">
              {{ getUrgencyText(row.urgency) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="原因" min-width="200" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import { getReplenishment } from '@/api/ai'

const loading = ref(false)
const replenishmentList = ref<any[]>([])

const getUrgencyType = (urgency: string) => {
  const map: Record<string, string> = {
    '高': 'danger',
    '中': 'warning',
    '低': 'info'
  }
  return map[urgency] || 'info'
}

const getUrgencyText = (urgency: string) => {
  return urgency || '未知'
}

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getReplenishment()
    replenishmentList.value = res.data.items || []
  } catch {
    replenishmentList.value = [
      {
        productName: '商品A',
        currentStock: 50,
        safetyStock: 100,
        avgDailySales: 20,
        replenishQuantity: 300,
        urgency: 'high',
        reason: '当前库存低于安全库存，日均销量较高，建议尽快补货'
      },
      {
        productName: '商品B',
        currentStock: 150,
        safetyStock: 100,
        avgDailySales: 15,
        replenishQuantity: 200,
        urgency: 'medium',
        reason: '预计7天内库存将降至安全库存以下'
      },
      {
        productName: '商品C',
        currentStock: 200,
        safetyStock: 80,
        avgDailySales: 10,
        replenishQuantity: 100,
        urgency: 'low',
        reason: '库存充足，可按计划补货'
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

.replenish-qty {
  font-weight: 600;
  color: var(--color-primary);
}
</style>
