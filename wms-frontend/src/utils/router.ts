/**
 * 路由工具 - 用于在非组件中进行路由跳转
 */
import router from '@/router'

/** 跳转到登录页 */
export function redirectToLogin() {
  localStorage.removeItem('token')
  router.push('/login')
}
