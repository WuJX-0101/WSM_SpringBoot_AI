import request from '@/utils/request'

export function forecast(productId: number, forecastDays?: number) {
  return request({ url: `/api/v1/ai/forecast/${productId}`, method: 'get', params: { forecastDays } })
}

export function getReplenishment() {
  return request({ url: '/api/v1/ai/replenishment', method: 'get' })
}

export function getAnomaly() {
  return request({ url: '/api/v1/ai/anomaly', method: 'get' })
}

export function chat(query: string) {
  return request({ url: '/api/v1/ai/chat', method: 'post', data: { query } })
}
