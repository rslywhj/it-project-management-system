import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse, type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, getRefreshToken, setToken, setRefreshToken, clearTokens } from '@/utils/auth'
import type { ApiResult, LoginResult } from '@/types/api'
import router from '@/router'

const service: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL + '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

// ─── Token 刷新状态管理 ───────────────────────────────────────
let isRefreshing = false
let pendingQueue: Array<(token: string) => void> = []

function subscribePending(cb: (token: string) => void): void {
  pendingQueue.push(cb)
}

function flushPending(newToken: string): void {
  pendingQueue.forEach(cb => cb(newToken))
  pendingQueue = []
}

// ─── 静默刷新 Token（绕过拦截器，避免循环） ────────────────────
async function silentRefresh(): Promise<LoginResult> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) throw new Error('no refresh token')

  const baseURL = import.meta.env.VITE_API_BASE_URL + '/api'
  const { data } = await axios.post<ApiResult<LoginResult>>(
    `${baseURL}/auth/refresh`,
    null,
    { params: { refreshToken } },
  )

  if (data.code !== 200) throw new Error(data.message || 'refresh failed')
  return data.data
}

// ─── 请求拦截器：注入 JWT Token ───────────────────────────────
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

// ─── 响应拦截器：401 自动续期 + 重试 ─────────────────────────
service.interceptors.response.use(
  (response: AxiosResponse<ApiResult>) => {
    const res = response.data
    if (res.code !== 200) {
      // 业务层 401：尝试刷新
      if (res.code === 401) {
        return handleTokenExpired(response.config as AxiosRequestConfig & { _retry?: boolean })
      }
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data as any
  },
  (error) => {
    // HTTP 401：尝试刷新
    if (error.response?.status === 401) {
      return handleTokenExpired(error.config as AxiosRequestConfig & { _retry?: boolean })
    }
    const msg = error.response?.data?.message || error.message || '网络异常'
    ElMessage.error(msg)
    return Promise.reject(error)
  },
)

// ─── 401 统一处理：刷新 Token 并重试失败请求 ─────────────────
function handleTokenExpired(failedConfig: AxiosRequestConfig & { _retry?: boolean }): Promise<any> {
  // 已经在重试的请求，直接跳过
  if (failedConfig._retry) {
    clearTokens()
    router.push('/login')
    return Promise.reject(new Error('token refresh failed'))
  }

  // 如果正在刷新中，排队等待
  if (isRefreshing) {
    return new Promise((resolve) => {
      subscribePending((newToken: string) => {
        if (failedConfig.headers) {
          failedConfig.headers.Authorization = `Bearer ${newToken}`
        }
        failedConfig._retry = true
        resolve(service(failedConfig))
      })
    })
  }

  // 发起刷新
  isRefreshing = true
  failedConfig._retry = true

  return silentRefresh()
    .then((loginResult) => {
      // 持久化新 Token
      setToken(loginResult.accessToken)
      setRefreshToken(loginResult.refreshToken)

      // 重放排队的请求
      flushPending(loginResult.accessToken)

      // 重试当前请求
      if (failedConfig.headers) {
        failedConfig.headers.Authorization = `Bearer ${loginResult.accessToken}`
      }
      return service(failedConfig)
    })
    .catch((refreshError) => {
      // 刷新失败 → 清除凭证，跳转登录
      clearTokens()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
      return Promise.reject(refreshError)
    })
    .finally(() => {
      isRefreshing = false
    })
}

export default service
