import axios from 'axios'

/**
 * 与后端约定：开发环境默认 baseURL='/api'，由 Vite 代理到 http://localhost:8080
 * 生产构建可设 VITE_API_BASE=http://your-host:8080/api
 */
const baseURL = import.meta.env.VITE_API_BASE || '/api'

export const http = axios.create({
  baseURL,
  timeout: 300_000
})

/** 拼接与 axios 一致的根路径，供 fetch(SSE) 使用 */
export function apiUrl(path) {
  const p = path.startsWith('/') ? path : `/${path}`
  return `${baseURL.replace(/\/$/, '')}${p}`
}
