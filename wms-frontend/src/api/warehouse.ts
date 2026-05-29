import request from '@/utils/request'

export function listWarehouse(params: { page?: number; size?: number; keyword?: string }) {
  return request({ url: '/api/v1/warehouse/list', method: 'get', params })
}

/** 获取所有仓库（带缓存，用于下拉选择） */
export function getAllWarehouse() {
  return request({ url: '/api/v1/warehouse/all', method: 'get' })
}

export function getWarehouse(id: number) {
  return request({ url: `/api/v1/warehouse/${id}`, method: 'get' })
}

export function createWarehouse(data: any) {
  return request({ url: '/api/v1/warehouse', method: 'post', data })
}

export function updateWarehouse(id: number, data: any) {
  return request({ url: `/api/v1/warehouse/${id}`, method: 'put', data })
}

export function deleteWarehouse(id: number) {
  return request({ url: `/api/v1/warehouse/${id}`, method: 'delete' })
}
