<template>
  <div class="inventory-log">
    <PageHeader title="库存日志" />

    <SearchForm :model="searchForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="商品">
        <el-select v-model="searchForm.productId" placeholder="全部商品" clearable filterable>
          <el-option v-for="p in productList" :key="p.id" :label="p.productName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="仓库">
        <el-select v-model="searchForm.warehouseId" placeholder="全部仓库" clearable>
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品" min-width="120" />
      <el-table-column prop="warehouseName" label="仓库" width="100" />
      <el-table-column prop="changeType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getChangeTypeTag(row.changeType)">
            {{ getChangeTypeLabel(row.changeType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="changeQuantity" label="数量" width="100">
        <template #default="{ row }">
          <span :style="{ color: row.changeQuantity > 0 ? 'var(--color-success)' : 'var(--color-error)' }">
            {{ row.changeQuantity > 0 ? '+' : '' }}{{ row.changeQuantity }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="beforeQuantity" label="调整前" width="100" />
      <el-table-column prop="afterQuantity" label="调整后" width="100" />
      <el-table-column prop="orderNo" label="关联单号" width="150" />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column prop="gmtCreate" label="时间" width="170" />
    </el-table>

    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSearch"
      @current-change="handleSearch"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import { listInventoryLog } from '@/api/inventory'
import { getAllWarehouse } from '@/api/warehouse'
import { listProduct } from '@/api/product'

const loading = ref(false)

// 变更类型映射
const getChangeTypeLabel = (type: number) => {
  const map: Record<number, string> = { 1: '入库', 2: '出库', 3: '盘点', 4: '调整' }
  return map[type] || '未知'
}

const getChangeTypeTag = (type: number) => {
  const map: Record<number, string> = { 1: 'success', 2: 'danger', 3: 'warning', 4: 'info' }
  return map[type] || 'info'
}

const searchForm = reactive({ productId: undefined as number | undefined, warehouseId: undefined as number | undefined })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const tableData = ref<any[]>([])
const warehouseList = ref<any[]>([])
const productList = ref<any[]>([])

const loadData = async () => {
  const [warehouseRes, productRes]: any[] = await Promise.all([
    getAllWarehouse(),
    listProduct({ page: 1, size: 100 })
  ])
  warehouseList.value = warehouseRes.data
  productList.value = productRes.data.records
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res: any = await listInventoryLog({
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  searchForm.productId = undefined
  searchForm.warehouseId = undefined
  pagination.page = 1
  handleSearch()
}

onMounted(() => {
  loadData()
  handleSearch()
})
</script>

<style scoped>
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
