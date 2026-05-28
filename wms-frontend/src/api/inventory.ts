import request from '@/utils/request'

export function listInventory(params: { page?: number; size?: number; warehouseId?: number; productId?: number }) {
  return request({ url: '/api/v1/inventory/list', method: 'get', params })
}

export function getInventory(id: number) {
  return request({ url: `/api/v1/inventory/${id}`, method: 'get' })
}

export function adjustInventory(id: number, data: { quantity: number; reason: string }) {
  return request({ url: `/api/v1/inventory/${id}/adjust`, method: 'put', data })
}

export function listInventoryLog(params: { page?: number; size?: number; productId?: number; warehouseId?: number }) {
  return request({ url: '/api/v1/inventory/log', method: 'get', params })
}
