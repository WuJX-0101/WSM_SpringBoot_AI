import request from '@/utils/request'

export function listInbound(params: { page?: number; size?: number; keyword?: string; status?: number }) {
  return request({ url: '/api/v1/inbound/list', method: 'get', params })
}

export function getInbound(id: number) {
  return request({ url: `/api/v1/inbound/${id}`, method: 'get' })
}

export function createInbound(data: any) {
  return request({ url: '/api/v1/inbound', method: 'post', data })
}

export function auditInbound(id: number) {
  return request({ url: `/api/v1/inbound/${id}/audit`, method: 'put' })
}

export function executeInbound(id: number) {
  return request({ url: `/api/v1/inbound/${id}/execute`, method: 'put' })
}

export function cancelInbound(id: number) {
  return request({ url: `/api/v1/inbound/${id}/cancel`, method: 'put' })
}
