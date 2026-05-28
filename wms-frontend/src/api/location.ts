import request from '@/utils/request'

export function listLocation(params: { page?: number; size?: number; keyword?: string; warehouseId?: number }) {
  return request({ url: '/api/v1/location/list', method: 'get', params })
}

export function getLocation(id: number) {
  return request({ url: `/api/v1/location/${id}`, method: 'get' })
}

export function createLocation(data: any) {
  return request({ url: '/api/v1/location', method: 'post', data })
}

export function updateLocation(id: number, data: any) {
  return request({ url: `/api/v1/location/${id}`, method: 'put', data })
}

export function deleteLocation(id: number) {
  return request({ url: `/api/v1/location/${id}`, method: 'delete' })
}

export function listByWarehouse(warehouseId: number) {
  return request({ url: `/api/v1/location/warehouse/${warehouseId}`, method: 'get' })
}
