import axios from 'axios'
import { ElMessage } from 'element-plus'
import { redirectToLogin } from './router'

// 创建axios实例，使用环境变量配置baseURL
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果返回的code不是200，说明有错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')

      // 401: 未授权，跳转到登录页
      if (res.code === 401) {
        redirectToLogin()
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  (error) => {
    // 网络错误
    if (error.message.includes('Network Error')) {
      ElMessage.error('网络错误，请检查网络连接')
    } else if (error.message.includes('timeout')) {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error(error.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
