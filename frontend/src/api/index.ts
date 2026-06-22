import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse, type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, setToken, getRefreshToken, setRefreshToken, clearTokens } from '@/utils/auth'
import type { ApiResult } from '@/types/api'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

// ---- Token 刷新机制 ----
let isRefreshing = false
let pendingQueue: Array<(token: string) => void> = []

function processPendingQueue(newToken: string) {
  pendingQueue.forEach((cb) => cb(newToken))
  pendingQueue = []
}

async function handleTokenRefresh(failedConfig: AxiosRequestConfig): Promise<AxiosResponse> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) {
    clearTokens()
    router.push('/login')
    return Promise.reject(new Error('无 refresh token'))
  }

  if (!isRefreshing) {
    isRefreshing = true
    try {
      // 直接用 axios 调用刷新接口，避免循环拦截
      const { data } = await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/auth/refresh`,
        null,
        { params: { refreshToken } },
      )
      if (data.code !== 200) throw new Error(data.message)
      const { accessToken, refreshToken: newRefreshToken } = data.data
      setToken(accessToken)
      if (newRefreshToken) setRefreshToken(newRefreshToken)
      processPendingQueue(accessToken)
      // 重试原请求
      failedConfig.headers = failedConfig.headers || {}
      failedConfig.headers.Authorization = `Bearer ${accessToken}`
      return service.request(failedConfig)
    } catch {
      clearTokens()
      router.push('/login')
      return Promise.reject(new Error('Token 刷新失败，请重新登录'))
    } finally {
      isRefreshing = false
    }
  }

  // 其他请求排队等待刷新完成
  return new Promise((resolve) => {
    pendingQueue.push((newToken: string) => {
      failedConfig.headers = failedConfig.headers || {}
      failedConfig.headers.Authorization = `Bearer ${newToken}`
      resolve(service.request(failedConfig))
    })
  })
}

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
      // 业务层 401：尝试刷新 token
      if (res.code === 401) {
        return handleTokenRefresh(response.config)
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    // 返回内层 data，视图可直接 const { data } = await api()
    return res.data as any
  },
  (error) => {
    // HTTP 401：尝试刷新 token
    if (error.response?.status === 401) {
      return handleTokenRefresh(error.config)
    }
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  },
)

export default service
