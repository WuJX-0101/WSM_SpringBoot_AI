import request from '@/utils/request'

// 用户登录
export function login(data: { username: string; password: string }) {
  return request({
    url: '/api/v1/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data: { username: string; password: string; nickname?: string; email?: string; phone?: string }) {
  return request({
    url: '/api/v1/auth/register',
    method: 'post',
    data
  })
}

// 获取当前用户信息
export function getUserInfo() {
  return request({
    url: '/api/v1/auth/info',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/v1/auth/logout',
    method: 'post'
  })
}

// 员工列表
export function listEmployee(params: { page?: number; size?: number; keyword?: string }) {
  return request({
    url: '/api/v1/auth/employee/list',
    method: 'get',
    params
  })
}

// 创建员工
export function createEmployee(data: { username: string; password: string; nickname?: string; email?: string; phone?: string; roleIds: string[] }) {
  return request({
    url: '/api/v1/auth/employee',
    method: 'post',
    data
  })
}

// 修改员工信息
export function updateEmployee(id: string, data: { nickname?: string; email?: string; phone?: string; roleIds: string[]; status?: number }) {
  return request({
    url: `/api/v1/auth/employee/${id}`,
    method: 'put',
    data
  })
}

// 删除员工
export function deleteEmployee(id: string) {
  return request({
    url: `/api/v1/auth/employee/${id}`,
    method: 'delete'
  })
}
