// ws.ts
import { useUserStore } from '@/stores'

/* ---------- 类型声明 ---------- */
type MessageHandler = (data: any) => void

/* ---------- 核心数据结构 ---------- */
const keyHandlerMap = new Map<string, Set<MessageHandler>>()

/* ---------- 注册 / 注销 ---------- */
export function addWsMessageHandler(key: string, handler: MessageHandler) {
  if (!keyHandlerMap.has(key)) keyHandlerMap.set(key, new Set())
  keyHandlerMap.get(key)!.add(handler)
}

export function removeWsMessageHandler(key: string, handler: MessageHandler) {
  const set = keyHandlerMap.get(key)
  if (!set) return
  set.delete(handler)
  if (set.size === 0) keyHandlerMap.delete(key)
}

/* ---------- 消息派发 ---------- */
function handleWsMessage(msg: { messageType: string; data: any }) {
  if (!msg || typeof msg.messageType !== 'string') {
    console.warn('[WS] 消息缺少 messageType，无法派发', msg)
    return
  }
  const { messageType, data } = msg
  const handlers = keyHandlerMap.get(messageType)
  if (!handlers || handlers.size === 0) {
    console.warn(`[WS] 未注册 messageType="${messageType}" 的处理器`)
    return
  }
  handlers.forEach(fn => fn(data))
}

/* ---------- WebSocket 实例 ---------- */
let ws: WebSocket | null = null
let pingTimer: number | null = null
const host = import.meta.env.VITE_WS_BASE_URL as string

/* ---------- 打开连接 ---------- */
export async function openWs() {
  if (ws) return

  const userStore = useUserStore()
  const token = userStore.refreshToken || userStore.deviceId
  if (!token) {
    console.error('无法建立 WebSocket 连接：缺少 token')
    return
  }

  try {
    ws = new WebSocket(`${host}/ws?token=${token}`)

    ws.onopen = () => {
      console.log('WebSocket 连接已建立')
      pingTimer = window.setInterval(() => {
        ws?.send('ping')
      }, 25_000)
    }

    ws.onclose = () => {
      console.log('WebSocket 连接已关闭')
      cleanup()
      setTimeout(openWs, 3000)
    }

    ws.onmessage = (event: MessageEvent) => {
      try {
        const body = JSON.parse(event.data)
        console.log(body)
        handleWsMessage(body)
      } catch (e) {
        console.error('WS 消息格式错误', event.data)
      }
    }

    ws.onerror = (ev) => {
      console.error('WebSocket 错误:', ev)
      cleanup()
      setTimeout(openWs, 5000)
    }
  } catch (error) {
    console.error('WebSocket 连接失败:', error)
    setTimeout(openWs, 5000)
  }
}

/* ---------- 关闭连接 ---------- */
export function closeWs() {
  if (ws) {
    ws.close()
    cleanup()
  }
}

/* ---------- 内部清理 ---------- */
function cleanup() {
  if (pingTimer) {
    clearInterval(pingTimer)
    pingTimer = null
  }
  ws = null
}
