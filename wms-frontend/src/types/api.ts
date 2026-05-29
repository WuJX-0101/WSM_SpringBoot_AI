/**
 * API 统一响应类型定义
 */

/** 统一响应结构 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/** 分页响应结构 */
export interface PageResult<T = any> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email?: string
  phone?: string
  avatar?: string
  status: number
  roles: string[]
  permissions: string[]
}

/** 登录响应 */
export interface LoginResult {
  accessToken: string
  tokenType: string
  expiresIn: number
  userInfo: UserInfo
}

/** 仓库信息 */
export interface Warehouse {
  id: number
  warehouseCode: string
  warehouseName: string
  warehouseType?: string
  address?: string
  contactPerson?: string
  contactPhone?: string
  area?: number
  status: number
}

/** 库位信息 */
export interface Location {
  id: number
  warehouseId: number
  locationCode: string
  locationName: string
  locationType?: string
  area?: number
  shelf?: string
  layer?: string
  position?: string
  capacity?: number
  status: number
}

/** 商品分类 */
export interface Category {
  id: number
  categoryCode: string
  categoryName: string
  parentId: number
  sortOrder?: number
  status: number
  children?: Category[]
}

/** 商品信息 */
export interface Product {
  id: number
  productCode: string
  productName: string
  categoryId: number
  brand?: string
  specification?: string
  unit?: string
  barcode?: string
  weight?: number
  volume?: number
  purchasePrice?: number
  salePrice?: number
  safetyStock?: number
  minStock?: number
  maxStock?: number
  image?: string
  status: number
}

/** 供应商信息 */
export interface Supplier {
  id: number
  supplierCode: string
  supplierName: string
  contactPerson?: string
  contactPhone?: string
  email?: string
  address?: string
  bankName?: string
  bankAccount?: string
  taxNumber?: string
}

/** 客户信息 */
export interface Customer {
  id: number
  customerCode: string
  customerName: string
  contactPerson?: string
  contactPhone?: string
  email?: string
  address?: string
  customerType?: string
}

/** 入库单 */
export interface InboundOrder {
  id: number
  orderNo: string
  orderType: number
  warehouseId: number
  warehouseName?: string
  supplierId?: number
  supplierName?: string
  orderStatus: number
  totalQuantity: number
  totalAmount: number
  inboundTime?: string
  remark?: string
  items?: InboundOrderItem[]
}

/** 入库单明细 */
export interface InboundOrderItem {
  id?: number
  orderId?: number
  productId: number
  productName?: string
  productCode?: string
  locationId?: number
  locationCode?: string
  locationName?: string
  batchNo?: string
  quantity: number
  price: number
  amount?: number
  productionDate?: string
  expiryDate?: string
}

/** 出库单 */
export interface OutboundOrder {
  id: number
  orderNo: string
  orderType: number
  warehouseId: number
  warehouseName?: string
  customerId?: number
  customerName?: string
  orderStatus: number
  totalQuantity: number
  totalAmount: number
  outboundTime?: string
  remark?: string
  items?: OutboundOrderItem[]
}

/** 出库单明细 */
export interface OutboundOrderItem {
  id?: number
  orderId?: number
  productId: number
  productName?: string
  productCode?: string
  locationId?: number
  locationCode?: string
  locationName?: string
  batchNo?: string
  quantity: number
  price: number
  amount?: number
}

/** 库存信息 */
export interface Inventory {
  id: number
  warehouseId: number
  warehouseName?: string
  locationId: number
  locationCode?: string
  locationName?: string
  productId: number
  productName?: string
  productCode?: string
  batchNo?: string
  quantity: number
  lockedQuantity: number
  availableQuantity: number
  costPrice?: number
  productionDate?: string
  expiryDate?: string
}

/** 库存日志 */
export interface InventoryLog {
  id: number
  warehouseId: number
  warehouseName?: string
  productId: number
  productName?: string
  productCode?: string
  batchNo?: string
  changeType: number
  changeQuantity: number
  beforeQuantity: number
  afterQuantity: number
  orderNo?: string
  remark?: string
  gmtCreate?: string
}

/** 订单统计 */
export interface OrderStats {
  totalCount: number
  totalAmount: number
  dailyStats: DailyStats[]
}

/** 每日统计 */
export interface DailyStats {
  date: string
  count: number
  amount: number
}

/** 需求预测结果 */
export interface DemandForecast {
  productId: number
  forecastQuantity: number
  confidence: number
  explanation: string
  suggestion: string
  suggestedReorderTime?: string
}

/** 补货建议 */
export interface ReplenishmentSuggestion {
  productId: number
  productName: string
  currentStock: number
  safetyStock: number
  suggestedQuantity: number
  estimatedCost: number
  urgency: 'high' | 'medium' | 'low'
}

/** 异常检测结果 */
export interface AnomalyDetection {
  totalAnomalies: number
  highPriority: number
  anomalies: AnomalyItem[]
}

/** 异常项 */
export interface AnomalyItem {
  type: string
  level: 'high' | 'medium' | 'low'
  productName: string
  description?: string
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/** 分页请求参数 */
export interface PageParams {
  page?: number
  size?: number
  keyword?: string
}
