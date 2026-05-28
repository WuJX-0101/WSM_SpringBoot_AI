import request from '@/utils/request'

/**
 * 获取首页统计数据
 * 
 * 包含：
 * - 商品总数
 * - 仓库数量
 * - 今日入库/出库单数和数量
 * - 库存总量
 * - 最近入库/出库单
 */
export function getDashboardStats() {
  return request({
    url: '/api/v1/dashboard/stats',
    method: 'get'
  })
}
