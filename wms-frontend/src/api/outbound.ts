import request from '@/utils/request'

export function listOutbound(params: { page?: number; size?: number; keyword?: string; status?: number }) {
  return request({ url: '/api/v1/outbound/list', method: 'get', params })
}

export function getOutbound(id: number) {
  return request({ url: `/api/v1/outbound/${id}`, method: 'get' })
}

export function createOutbound(data: any) {
  return request({ url: '/api/v1/outbound', method: 'post', data })
}

export function auditOutbound(id: number) {
  return request({ url: `/api/v1/outbound/${id}/audit`, method: 'put' })
}

export function executeOutbound(id: number) {
  return request({ url: `/api/v1/outbound/${id}/execute`, method: 'put' })
}

export function cancelOutbound(id: number) {
  return request({ url: `/api/v1/outbound/${id}/cancel`, method: 'put' })
}
