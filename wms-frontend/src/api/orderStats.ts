import request from '@/utils/request'

export function getOrderStats() {
  return request({ url: '/api/v1/order-stats', method: 'get' })
}

export function getDailyInbound(params?: { startDate?: string; endDate?: string }) {
  return request({ url: '/api/v1/order-stats/daily-inbound', method: 'get', params })
}

export function getDailyOutbound(params?: { startDate?: string; endDate?: string }) {
  return request({ url: '/api/v1/order-stats/daily-outbound', method: 'get', params })
}

export function getInboundRank(params?: { limit?: number }) {
  return request({ url: '/api/v1/order-stats/inbound-rank', method: 'get', params })
}

export function getOutboundRank(params?: { limit?: number }) {
  return request({ url: '/api/v1/order-stats/outbound-rank', method: 'get', params })
}
