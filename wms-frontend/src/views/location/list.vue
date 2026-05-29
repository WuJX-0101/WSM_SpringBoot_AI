<template>
  <div class="location-list">
    <PageHeader title="库位管理">
      <template #actions>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增库位
        </el-button>
      </template>
    </PageHeader>

    <SearchForm :model="searchForm" @search="handleSearch" @reset="handleReset">
      <el-form-item label="仓库">
        <el-select v-model="searchForm.warehouseId" placeholder="请选择仓库" clearable>
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="库位编码">
        <el-input v-model="searchForm.keyword" placeholder="请输入库位编码" clearable />
      </el-form-item>
    </SearchForm>

    <el-table :data="tableData" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="locationCode" label="库位编码" width="120" />
      <el-table-column prop="warehouseName" label="所属仓库" width="120" />
      <el-table-column prop="area" label="区域" width="100" />
      <el-table-column prop="capacity" label="容量" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.status" :status-map="statusMap" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @close="handleDialogClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="库位编码" prop="locationCode">
          <el-input v-model="form.locationCode" placeholder="请输入库位编码" />
        </el-form-item>
        <el-form-item label="区域" prop="area">
          <el-input v-model="form.area" placeholder="请输入区域" />
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import SearchForm from '@/components/SearchForm.vue'
import StatusTag from '@/components/StatusTag.vue'
import { listLocation, createLocation, updateLocation, deleteLocation } from '@/api/location'
import { getAllWarehouse } from '@/api/warehouse'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const editId = ref<number | null>(null)

const searchForm = reactive({ keyword: '', warehouseId: undefined as number | undefined })
const pagination = reactive({ page: 1, size: 10, total: 0 })
const tableData = ref<any[]>([])
const warehouseList = ref<any[]>([])

const statusMap = {
  1: { label: '启用', type: 'success' },
  0: { label: '禁用', type: 'danger' }
}

const form = reactive({
  warehouseId: undefined as number | undefined,
  locationCode: '',
  area: '',
  capacity: 100,
  status: 1
})

const rules: FormRules = {
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  locationCode: [{ required: true, message: '请输入库位编码', trigger: 'blur' }]
}

const loadWarehouses = async () => {
  const res: any = await getAllWarehouse()
  warehouseList.value = res.data
}

const handleSearch = async () => {
  loading.value = true
  try {
    const res: any = await listLocation({
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
  searchForm.warehouseId = undefined
  pagination.page = 1
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增库位'
  editId.value = null
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑库位'
  editId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该库位吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteLocation(row.id)
    ElMessage.success('删除成功')
    handleSearch()
  }).catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (editId.value) {
      await updateLocation(editId.value, form)
      ElMessage.success('更新成功')
    } else {
      await createLocation(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleSearch()
  } finally {
    submitLoading.value = false
  }
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadWarehouses()
  handleSearch()
})
</script>

<style scoped>
.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
