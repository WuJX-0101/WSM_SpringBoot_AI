import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/index.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('../layout/index.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/dashboard/index.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'warehouse/list',
          name: 'warehouse-list',
          component: () => import('../views/warehouse/list.vue'),
          meta: { title: '仓库管理' }
        },
        {
          path: 'location/list',
          name: 'location-list',
          component: () => import('../views/location/list.vue'),
          meta: { title: '库位管理' }
        },
        {
          path: 'category/list',
          name: 'category-list',
          component: () => import('../views/category/list.vue'),
          meta: { title: '商品分类' }
        },
        {
          path: 'product/list',
          name: 'product-list',
          component: () => import('../views/product/list.vue'),
          meta: { title: '商品管理' }
        },
        {
          path: 'supplier/list',
          name: 'supplier-list',
          component: () => import('../views/supplier/list.vue'),
          meta: { title: '供应商管理' }
        },
        {
          path: 'customer/list',
          name: 'customer-list',
          component: () => import('../views/customer/list.vue'),
          meta: { title: '客户管理' }
        },
        {
          path: 'inbound/list',
          name: 'inbound-list',
          component: () => import('../views/inbound/list.vue'),
          meta: { title: '入库单列表' }
        },
        {
          path: 'inbound/create',
          name: 'inbound-create',
          component: () => import('../views/inbound/create.vue'),
          meta: { title: '创建入库单' }
        },
        {
          path: 'outbound/list',
          name: 'outbound-list',
          component: () => import('../views/outbound/list.vue'),
          meta: { title: '出库单列表' }
        },
        {
          path: 'outbound/create',
          name: 'outbound-create',
          component: () => import('../views/outbound/create.vue'),
          meta: { title: '创建出库单' }
        },
        {
          path: 'inventory/list',
          name: 'inventory-list',
          component: () => import('../views/inventory/list.vue'),
          meta: { title: '库存列表' }
        },
        {
          path: 'inventory/log',
          name: 'inventory-log',
          component: () => import('../views/inventory/log.vue'),
          meta: { title: '库存日志' }
        },
        {
          path: 'report/order',
          name: 'report-order',
          component: () => import('../views/report/order.vue'),
          meta: { title: '订单统计' }
        },
        {
          path: 'report/inventory',
          name: 'report-inventory',
          component: () => import('../views/report/inventory.vue'),
          meta: { title: '库存报表' }
        },
        {
          path: 'ai/forecast',
          name: 'ai-forecast',
          component: () => import('../views/ai/forecast.vue'),
          meta: { title: '需求预测' }
        },
        {
          path: 'ai/replenishment',
          name: 'ai-replenishment',
          component: () => import('../views/ai/replenishment.vue'),
          meta: { title: '智能补货' }
        },
        {
          path: 'ai/anomaly',
          name: 'ai-anomaly',
          component: () => import('../views/ai/anomaly.vue'),
          meta: { title: '异常检测' }
        },
        {
          path: 'ai/chat',
          name: 'ai-chat',
          component: () => import('../views/ai/chat.vue'),
          meta: { title: 'AI助手' }
        },
        {
          path: 'system/employee',
          name: 'system-employee',
          component: () => import('../views/system/employee/list.vue'),
          meta: { title: '员工管理' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to) => {
  // 设置页面标题
  document.title = `${to.meta.title || 'WMS仓储管理系统'} - WMS`
  
  // 检查是否需要登录
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    return '/login'
  }
  
  return true
})

export default router
