import request from '@/utils/request'

export function listCustomer(params: { page?: number; size?: number; keyword?: string }) {
  return request({ url: '/api/v1/customer/list', method: 'get', params })
}

export function getCustomer(id: number) {
  return request({ url: `/api/v1/customer/${id}`, method: 'get' })
}

export function createCustomer(data: any) {
  return request({ url: '/api/v1/customer', method: 'post', data })
}

export function updateCustomer(id: number, data: any) {
  return request({ url: `/api/v1/customer/${id}`, method: 'put', data })
}

export function deleteCustomer(id: number) {
  return request({ url: `/api/v1/customer/${id}`, method: 'delete' })
}
