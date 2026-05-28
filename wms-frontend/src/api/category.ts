import request from '@/utils/request'

export function listCategory(params?: { keyword?: string }) {
  return request({ url: '/api/v1/category/list', method: 'get', params })
}

export function getCategoryTree() {
  return request({ url: '/api/v1/category/tree', method: 'get' })
}

export function getCategory(id: number) {
  return request({ url: `/api/v1/category/${id}`, method: 'get' })
}

export function createCategory(data: any) {
  return request({ url: '/api/v1/category', method: 'post', data })
}

export function updateCategory(id: number, data: any) {
  return request({ url: `/api/v1/category/${id}`, method: 'put', data })
}

export function deleteCategory(id: number) {
  return request({ url: `/api/v1/category/${id}`, method: 'delete' })
}
