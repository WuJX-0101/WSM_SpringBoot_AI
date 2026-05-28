<template>
  <div class="outbound-create">
    <PageHeader title="创建出库单" />

    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-card>
        <template #header>
          <span>基本信息</span>
        </template>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12">
            <el-form-item label="客户" prop="customerId">
              <el-select v-model="form.customerId" placeholder="请选择客户" filterable>
                <el-option v-for="c in customerList" :key="c.id" :label="c.customerName" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12">
            <el-form-item label="出库仓库" prop="warehouseId">
              <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable>
                <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <el-card style="margin-top: 16px">
        <template #header>
          <div class="card-header">
            <span>出库明细</span>
            <el-button type="primary" size="small" @click="handleAddItem">
              <el-icon><Plus /></el-icon>
              添加行
            </el-button>
          </div>
        </template>

        <el-table :data="form.items" border>
          <el-table-column label="商品" min-width="180">
            <template #default="{ row }">
              <el-select v-model="row.productId" placeholder="请选择商品" filterable size="small">
                <el-option v-for="p in productList" :key="p.id" :label="p.productName" :value="p.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120">
            <template #default="{ row }">
              <el-input-number v-model="row.price" :min="0" :precision="2" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="金额" width="120">
            <template #default="{ row }">
              ¥{{ (row.quantity * row.price).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ $index }">
              <el-button type="danger" link size="small" @click="handleRemoveItem($index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="total-row">
          合计数量: {{ totalQuantity }} | 合计金额: ¥{{ totalAmount.toFixed(2) }}
        </div>
      </el-card>

      <div class="form-actions">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { createOutbound } from '@/api/outbound'
import { listCustomer } from '@/api/customer'
import { listWarehouse } from '@/api/warehouse'
import { listProduct } from '@/api/product'

const router = useRouter()
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const customerList = ref<any[]>([])
const warehouseList = ref<any[]>([])
const productList = ref<any[]>([])

const form = reactive({
  customerId: undefined as number | undefined,
  warehouseId: undefined as number | undefined,
  remark: '',
  items: [] as Array<{ productId: number | undefined; quantity: number; price: number }>
})

const rules: FormRules = {
  customerId: [{ required: true, message: '请选择客户', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }]
}

const totalQuantity = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

const totalAmount = computed(() => {
  return form.items.reduce((sum, item) => sum + (item.quantity || 0) * (item.price || 0), 0)
})

const loadData = async () => {
  const [customerRes, warehouseRes, productRes]: any[] = await Promise.all([
    listCustomer({ page: 1, size: 100 }),
    listWarehouse({ page: 1, size: 100 }),
    listProduct({ page: 1, size: 100 })
  ])
  customerList.value = customerRes.data.records
  warehouseList.value = warehouseRes.data.records
  productList.value = productRes.data.records
}

const handleAddItem = () => {
  form.items.push({ productId: undefined, quantity: 1, price: 0 })
}

const handleRemoveItem = (index: number) => {
  form.items.splice(index, 1)
}

const handleCancel = () => {
  router.push('/outbound/list')
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  if (form.items.length === 0) {
    ElMessage.warning('请添加出库明细')
    return
  }
  submitLoading.value = true
  try {
    await createOutbound(form)
    ElMessage.success('创建成功')
    router.push('/outbound/list')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
  handleAddItem()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-row {
  margin-top: 12px;
  text-align: right;
  font-weight: 500;
  color: var(--color-text);
}

.form-actions {
  margin-top: 24px;
  text-align: center;
}
</style>
