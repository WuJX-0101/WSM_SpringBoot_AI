<template>
  <div class="outbound-list">
    <PageHeader title="出库单列表">
      <template #actions>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          创建出库单
        </el-button>
      </template>
    </PageHeader>

    <SearchForm :model="searchForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="单号">
        <el-input v-model="searchForm.keyword" placeholder="请输入出库单号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="全部" clearable>
          <el-option label="待审核" :value="0" />
          <el-option label="已审核" :value="1" />
          <el-option label="已完成" :value="2" />
          <el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
    </SearchForm>

    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="出库单号" width="150" />
      <el-table-column prop="customerName" label="客户" min-width="120" />
      <el-table-column prop="warehouseName" label="出库仓库" width="100" />
      <el-table-column prop="totalQuantity" label="总数量" width="100" />
      <el-table-column prop="orderStatus" label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.orderStatus" />
        </template>
      </el-table-column>
      <el-table-column prop="gmtCreate" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button v-if="row.orderStatus === 0" type="success" link @click="handleAudit(row)">审核</el-button>
          <el-button v-if="row.orderStatus === 1" type="warning" link @click="handleExecute(row)">执行</el-button>
          <el-button v-if="row.orderStatus <= 1" type="danger" link @click="handleCancel(row)">取消</el-button>
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

    <el-dialog v-model="detailVisible" title="出库单详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="出库单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ detail.customerName }}</el-descriptions-item>
        <el-descriptions-item label="出库仓库">{{ detail.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <StatusTag :status="detail.orderStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="总数量">{{ detail.totalQuantity }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h4 style="margin: 16px 0 8px">出库明细</h4>
      <el-table :data="detail.items || []" border>
        <el-table-column prop="productName" label="商品" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import { listOutbound, getOutbound, auditOutbound, executeOutbound, cancelOutbound } from '@/api/outbound'

const router = useRouter()
const loading = ref(false)
const detailVisible = ref(false)

const searchForm = reactive({ keyword: '', status: undefined as number | undefined })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const tableData = ref<any[]>([])
const detail = ref<any>({})

const handleSearch = async () => {
  loading.value = true
  try {
    const res: any = await listOutbound({
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
  searchForm.keyword = ''
  searchForm.status = undefined
  pagination.page = 1
  handleSearch()
}

const handleCreate = () => {
  router.push('/outbound/create')
}

const handleView = async (row: any) => {
  const res: any = await getOutbound(row.id)
  detail.value = res.data
  detailVisible.value = true
}

const handleAudit = (row: any) => {
  ElMessageBox.confirm('确定要审核通过该出库单吗？', '提示', { type: 'warning' }).then(async () => {
    await auditOutbound(row.id)
    ElMessage.success('审核成功')
    handleSearch()
  }).catch(() => {})
}

const handleExecute = (row: any) => {
  ElMessageBox.confirm('确定要执行该出库单吗？执行后将扣减库存。', '提示', { type: 'warning' }).then(async () => {
    await executeOutbound(row.id)
    ElMessage.success('执行成功')
    handleSearch()
  }).catch(() => {})
}

const handleCancel = (row: any) => {
  ElMessageBox.confirm('确定要取消该出库单吗？', '提示', { type: 'warning' }).then(async () => {
    await cancelOutbound(row.id)
    ElMessage.success('已取消')
    handleSearch()
  }).catch(() => {})
}

onMounted(() => {
  handleSearch()
})
</script>

<style scoped>
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
