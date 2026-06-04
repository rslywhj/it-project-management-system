import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearTokens } from '@/utils/auth'
import type { ApiResult } from '@/types/api'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

// 请求拦截器：注入 JWT Token
service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一错误处理，返回 data payload
service.interceptors.response.use(
  (response: AxiosResponse<ApiResult>) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      // 401: Token 失效，跳转登录
      if (res.code === 401) {
        clearTokens()
        router.push('/login')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    // 返回内层 data，视图可直接 const { data } = await api()
    return res.data as any
  },
  (error) => {
    if (error.response?.status === 401) {
      clearTokens()
      router.push('/login')
    }
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  },
)

export default service
