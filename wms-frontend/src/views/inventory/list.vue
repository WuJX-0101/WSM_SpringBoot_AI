<template>
  <div class="inventory-list">
    <PageHeader title="库存列表" />

    <SearchForm :model="searchForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="仓库">
        <el-select v-model="searchForm.warehouseId" placeholder="全部仓库" clearable>
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="商品">
        <el-select v-model="searchForm.productId" placeholder="全部商品" clearable filterable>
          <el-option v-for="p in productList" :key="p.id" :label="p.productName" :value="p.id" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="productName" label="商品名称" min-width="150" />
      <el-table-column prop="warehouseName" label="仓库" width="100" />
      <el-table-column prop="locationCode" label="库位" width="100" />
      <el-table-column prop="quantity" label="库存数量" width="100" />
      <el-table-column prop="lockedQuantity" label="冻结数量" width="100" />
      <el-table-column label="可用数量" width="100">
        <template #default="{ row }">
          {{ row.quantity - row.lockedQuantity }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleAdjust(row)">调整</el-button>
        </template>
      </el-table-column>
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

    <el-dialog v-model="adjustVisible" title="库存调整" width="500px" @close="handleAdjustClose">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="商品">{{ adjustData.productName }}</el-descriptions-item>
        <el-descriptions-item label="仓库">{{ adjustData.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="当前库存">{{ adjustData.quantity }}</el-descriptions-item>
      </el-descriptions>

      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-width="100px" style="margin-top: 16px">
        <el-form-item label="调整数量" prop="quantity">
          <el-input-number v-model="adjustForm.quantity" />
          <span class="adjust-tip">正数增加，负数减少</span>
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input v-model="adjustForm.reason" type="textarea" :rows="3" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdjustSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import { listInventory, adjustInventory } from '@/api/inventory'
import { listWarehouse } from '@/api/warehouse'
import { listProduct } from '@/api/product'

const loading = ref(false)
const submitLoading = ref(false)
const adjustVisible = ref(false)
const adjustFormRef = ref<FormInstance>()

const searchForm = reactive({ warehouseId: undefined as number | undefined, productId: undefined as number | undefined })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const tableData = ref<any[]>([])
const warehouseList = ref<any[]>([])
const productList = ref<any[]>([])
const adjustData = ref<any>({})

const adjustForm = reactive({
  quantity: 0,
  reason: ''
})

const adjustRules: FormRules = {
  quantity: [{ required: true, message: '请输入调整数量', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

const loadData = async () => {
  const [warehouseRes, productRes]: any[] = await Promise.all([
    listWarehouse({ page: 1, size: 100 }),
    listProduct({ page: 1, size: 100 })
  ])
  warehouseList.value = warehouseRes.data.records
  productList.value = productRes.data.records
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res: any = await listInventory({
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
  searchForm.warehouseId = undefined
  searchForm.productId = undefined
  pagination.page = 1
  handleSearch()
}

const handleAdjust = (row: any) => {
  adjustData.value = row
  adjustForm.quantity = 0
  adjustForm.reason = ''
  adjustVisible.value = true
}

const handleAdjustSubmit = async () => {
  await adjustFormRef.value?.validate()
  submitLoading.value = true
  try {
    await adjustInventory(adjustData.value.id, adjustForm)
    ElMessage.success('调整成功')
    adjustVisible.value = false
    handleSearch()
  } finally {
    submitLoading.value = false
  }
}

const handleAdjustClose = () => {
  adjustFormRef.value?.resetFields()
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

.adjust-tip {
  margin-left: 8px;
  font-size: 12px;
  color: var(--color-text-secondary);
}
</style>
