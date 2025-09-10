// utils/request.ts
import axios, { AxiosError } from 'axios'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'
import router from '@/router'

const instance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
})

/* 标记是否正在刷新，防止并发重复请求 */
let isRefreshing = false
/* 暂存待重放的请求队列 */
let requests: ((token: string) => void)[] = []

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    const userState = useUserStore()
    // 携带短token
    if (userState.accessToken) {
      config.headers.Authorization = userState.accessToken
    }
    return config
  },
  (err) => Promise.reject(err),
)

// 响应拦截器
instance.interceptors.response.use(
  res => res,
  async (err: AxiosError) => {
    const { response, config } = err
    const user = useUserStore()

    // 短 Token 失效（后端返回 401 且 msg = 'accessTokenExpired'）
    if (response?.status === 401 && (response.data as any)?.msg === 'accessTokenExpired') {
      if (!user.refreshToken) {
        user.removeToken()
        router.push('/login')
        return Promise.reject(err)
      }

      /* 第一次失效 → 发起刷新 */
      if (!isRefreshing) {
        isRefreshing = true
        try {
          const { data } = await axios.post(import.meta.env.VITE_API_BASE_URL + '/api/auth/refresh', {}, {
            headers: { 'x-refresh-token': user.refreshToken }
          })
          const { accessToken } = data.data
          user.setTokens(accessToken, user.refreshToken)   // 只更新短 Token

          /* 重放队列里所有等待请求 */
          requests.forEach(cb => cb(accessToken))
          requests = []
          return instance(config!)        // 重放原请求
        } catch (refreshErr: any) {
          /* 长 Token 也失效 */
          if (refreshErr.response?.status === 401) {
            ElMessage.error('登录已过期，请重新登录')
            user.removeToken()
            router.push('/login')
          }
          requests.forEach(cb => cb(''))  // 清空队列
          return Promise.reject(refreshErr)
        } finally {
          isRefreshing = false
        }
      } else {
        /* 并发请求 → 直接排队，等刷新完重放 */
        return new Promise(resolve => {
          requests.push((token: string) => {
            config!.headers!.Authorization = token
            resolve(instance(config!))
          })
        })
      }
    }

    /* 其他 401 / 500 统一处理 */
    if (response?.status === 401 && (response.data as any)?.msg === 'login') {
      user.removeToken()
      router.push('/login')
    }
    ElMessage.error((response?.data as any)?.msg || '服务异常')
    return Promise.reject(err)
  }
)

//axios响应拦截器
// instance.interceptors.response.use(

//     error => {
//       console.log('err' + error)
//       let { message } = error;
//       if (message == "Network Error") {
//         message = "后端接口连接异常";
//       }
//       else if (message.includes("timeout")) {
//         message = "系统接口请求超时";
//       }
//       else if (message.includes("Request failed with status code")) {
//         message = "系统接口" + message.substr(message.length - 3) + "异常";
//       }
//       return Promise.reject(error)

// })
export default instance
