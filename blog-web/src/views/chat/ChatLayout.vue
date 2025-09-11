<template>
  <div class="container">
    <div class="chat-size">
      <el-container v-if="!starting">
        <el-aside class="chat-aside" width="260px">
          <ChatList :current-chat-id="state.currentChatId"></ChatList>
        </el-aside>
        <el-container>
          <el-header class="chat-header">
            <h1 class="chat-header-name">{{ chating.nickname }}</h1>
            <div v-if="!userStore.getIsLogin()" class="chat-header-tip">未登录状态下可能会导致聊天记录丢失</div>
          </el-header>
          <el-main class="chat-main">
            <ChatRecord v-if="chating.chatType === 'single'" :chating="chating" @load-more="onLoadMore"
              :has-more="hasMore" ref="chatRef">
            </ChatRecord>
            <GroupChatRecord v-else-if="chating.chatType === 'group'" :chat-record-list="chating.chatMessageList"
              @load-more="onLoadMore" :has-more="hasMore" ref="chatRef">
            </GroupChatRecord>
          </el-main>
          <el-footer class="chat-footer">
            <ChatInput v-if="chating.chatType === 'single'" :chating="chating">
            </ChatInput>
            <GroupChatInput v-else-if="chating.chatType === 'group'" :chating="chating"></GroupChatInput>
          </el-footer>
        </el-container>
      </el-container>
    </div>

  </div>
</template>

<script setup lang="ts">
import type { Chat, ChatMessage, WsMsg } from '@/types/chatType'
import { reactive, toRefs, watch, onMounted, onUnmounted, nextTick, ref } from 'vue'
import { useUserStore } from '@/stores'
import { useChatStore } from '@/stores/modules/chat'
import { useRoute } from 'vue-router'

import ChatInput from './ChatInput.vue'
import ChatRecord from './ChatRecord.vue';
import GroupChatRecord from './GroupChatRecord.vue';
import GroupChatInput from './GroupChatInput.vue'
import ChatList from './ChatList.vue'
import { getChatListService, getNewChatCountByGroupService, getNewChatCountBySingleService } from '@/api/chat'
import { ElMessage } from 'element-plus'

/* ---------------- stores / router ---------------- */
const userStore = useUserStore()
const chatStore = useChatStore()
const route = useRoute()


/* ---------------- reactive state ---------------- */
const state = reactive({
  /* 当前在看的会话 id */
  currentChatId: '' as string,

  /* 当前正在展示的会话（消息列表） */
  chating: {
    id: '',
    chatType: 'single',
    chatMessageList: [] as ChatMessage[],
    nickname: '',
    avatar: '',
    unReadChat: 0,
    lastMessage: {
      messageType: 'text',
      message: '',
      id: 0,
      userId: 0,
      nickname: '',
      userAvatar: '',
      createTime: ''
    }
  } as Chat,

  /* 未读数（顶部 badge） */
  unReadMessage: 0,

  /* 是否还有更多历史消息 */
  hasMore: true
})

/* 方便解构到模板 */
const { chating, unReadMessage, hasMore } = toRefs(state)
const chatRef = ref()
const starting = ref(true)

/* ---------------- WebSocket ---------------- */
let ws: WebSocket | null = null
let pingTimer: number | null = null
const host = import.meta.env.VITE_WS_BASE_URL

function openWs() {
  let token = userStore.refreshToken ? userStore.refreshToken : userStore.deviceId
  if (ws && ws.readyState === WebSocket.OPEN) return
  ws = new WebSocket(`${host}/ws?token=${token}`)
  ws.onopen = () => {
    pingTimer = window.setInterval(() => ws?.send('ping'), 25_000)
  }
  ws.onclose = () => {
    if (pingTimer) clearInterval(pingTimer)
  }
  ws.onmessage = (e) => {
    /* TODO: 真正收到消息时，直接 push 到对应会话 */
    try {
      const body: WsMsg = JSON.parse(e.data)
      console.log("ws:" + body.receiver)
      console.log(body)
      handleWsMessage(body)
    } catch (err) {
      console.error('WS 消息格式错误', e.data)
    }
  }
}

function closeWs() {
  if (ws) {
    ws.close()
    ws = null
  }
  if (pingTimer) {
    clearInterval(pingTimer)
    pingTimer = null
  }
}

/* WS 收到消息后统一入口 */
function handleWsMessage(raw: WsMsg) {
  const { receiver, message } = raw

  /* ---- 1. 当前会话 ---- */
  if (state.currentChatId === receiver) {
    chating.value.chatMessageList.push(message)
    chating.value.lastMessage = message
    handleScrollToBottom()
    return
  }

  /* ---- 2. 非当前会话 ---- */
  const target = chatStore.chatList.find(c => c.id === receiver)
  if (!target) return

  const lastMsg = target.chatMessageList.at(-1)
  // 末尾消息与旧 lastMessage 完全相等（id 相同即可）
  const needUpdateList = lastMsg && lastMsg.id === target.lastMessage.id

  if (needUpdateList) {
    target.chatMessageList.push(message)
  }
  target.lastMessage = message
  target.unReadChat++
  handleScrollToBottom()
}

/* ---------------- 工具函数 ---------------- */
function mergeMessages(oldList: ChatMessage[], newList: ChatMessage[]) {
  const map = new Map<number, ChatMessage>()
  oldList.forEach(m => map.set(m.id, m))
  newList.forEach(m => map.set(m.id, m))
  return Array.from(map.values()).sort((a, b) => a.id - b.id)
}

const clearSelection = () => {
  window.getSelection()?.removeAllRanges()
}

const onMouseDown = (e: MouseEvent) => {
  const block = document.querySelector('.chat-record')

  if (block && !block.contains(e.target as Node)) {
    clearSelection()
  }
}

const setDefaultMessage = () => {
  return {
    messageType: 'text',
    message: '',
    id: 0,
    userId: 0,
    nickname: '',
    userAvatar: '',
    createTime: ''

  }
}

const handleMyMessage = () => {
  chatStore.chatList.forEach(chat => {
    chat.chatMessageList = chat.chatMessageList.filter(Boolean)
    chat.lastMessage = chat.lastMessage || setDefaultMessage()
  })
}

const handleScrollToBottom = () => {
  chatRef.value?.scrollToBottom()
}

/* ---------------- 业务 ---------------- */
/* 1. 把当前会话写回 chatStore（持久化） */
const saveSession = () => {
  const idx = chatStore.chatList.findIndex(c => c.id === state.chating.id)
  if (idx === -1) return

  chatStore.chatList[idx] = { ...state.chating }
}

/* 2. 初始化：把 chatStore 中的会话复制到本地 reactive */
const initSession = (id: string) => {

  state.currentChatId = id

  saveSession()

  refreshNewCount()

  initChating(id)

  loadLatestMessages()

  state.unReadMessage = state.chating.unReadChat
  state.chating.unReadChat = 0

}

const initChating = (id: string) => {
  const target = chatStore.chatList.find(c => c.id === id)
  if (!target) {
    state.chating = {
      id,
      route: '',
      chatType: 'single',
      chatMessageList: [],
      nickname: '',
      avatar: '',
      unReadChat: 0,
      lastMessage: setDefaultMessage()
    }
    return
  }

  state.chating = { ...target, chatMessageList: [...target.chatMessageList] }
}

/* 3. 加载最新消息（或历史） */
const loadLatestMessages = async () => {
  const { chating } = state
  if (!chating.id) return

  if (chating.chatMessageList.at(-1)?.id == chating.lastMessage.id) {
    state.unReadMessage = state.chating.unReadChat
    state.chating.unReadChat = 0
    return
  }

  let cursor = 0
  let direction: 'before' | 'after' = 'after'
  let limit = 20

  /* 判断是否需要加载历史消息 */
  const shouldLoadHistory =
    state.chating.unReadChat > 1000 ||
    state.chating.chatMessageList.length === 0

  if (shouldLoadHistory) {
    direction = 'before'
    limit = 50
    cursor = (chating.lastMessage.id + 1) || 0
  } else {
    cursor = chating.chatMessageList.at(-1)?.id || 0
  }

  const { data } = await getChatListService({
    cursor: cursor,
    direction: direction,
    limit: limit,
    type: chating.chatType
  }, userStore.deviceId)

  /* 处理不同加载方向的消息合并 */
  if (direction === 'before') {
    // 历史消息插入到头部
    state.chating.chatMessageList = [
      ...data.data.list,
      ...state.chating.chatMessageList
    ]
  } else {
    // 新消息合并到尾部
    state.chating.chatMessageList = mergeMessages(
      state.chating.chatMessageList,
      data.data.list
    )

    // 当未读消息小于1000且消息列表非空时，循环加载直到达到最新消息
    if (state.chating.unReadChat <= 1000 &&
      state.chating.chatMessageList.length > 0 &&
      state.chating.chatMessageList.at(-1)!.id < chating.lastMessage.id) {
      await loadLatestMessages()
    } else {
      state.unReadMessage = state.chating.unReadChat
      state.chating.unReadChat = 0
    }
  }

  state.hasMore = data.data.hasMore
}

/* 轮询 / 获取每天会话的新消息 */
const refreshNewCount = async () => {
  let maxSingleId = 0;
  let maxGroupId = 0;
  chatStore.chatList.forEach(c => {
    const ids = [
      ...c.chatMessageList.map(m => m.id),
      c.lastMessage?.id ?? 0
    ];
    const maxId = Math.max(...ids);

    if (c.chatType === 'single' && maxId > maxSingleId) maxSingleId = maxId;
    if (c.chatType === 'group' && maxId > maxGroupId) maxGroupId = maxId;
  });

  // 分别获取单聊和群聊的新消息
  const [singleChatData, groupChatData] = await Promise.all([
    getNewChatCountBySingleService({ device: userStore.deviceId, id: maxSingleId }),
    getNewChatCountByGroupService({ device: userStore.deviceId, id: maxGroupId })
  ]);

  // 处理单聊数据
  for (const brief of singleChatData.data.data) {
    const exist = chatStore.chatList.find(c => c.id === brief.info.id && c.chatType === brief.info.type);
    console.log(exist)
    console.log(brief.info.id)
    if (exist) {
      // 单聊返回消息列表，需要添加到现有消息中
      if (brief.messages && brief.messages.length > 0) {
        exist.chatMessageList = [...exist.chatMessageList, ...brief.messages];
        exist.unReadChat += brief.messages.length;
        if (brief.messages[brief.messages.length - 1].id > (exist.lastMessage?.id || 0)) {
          exist.lastMessage = brief.messages[brief.messages.length - 1];
        }
      }
    }
  }

  // 处理群聊数据
  for (const brief of groupChatData.data.data) {
    const exist = chatStore.chatList.find(c => c.id === brief.info.id && c.chatType === 'group');
    if (exist) {
      exist.unReadChat += brief.count;
      if (brief.lastMessage && brief.lastMessage.id > (exist.lastMessage?.id || 0)) {
        exist.lastMessage = brief.lastMessage;
      }
    }
  }

}

/* 5. 向上滚动加载历史 */
async function onLoadMore(done: () => void) {
  if (!state.hasMore) return done()

  chatRef.value?.lockScroll()
  const firstId = state.chating.chatMessageList[0]?.id
  // if (!firstId) return done()

  try {
    const res = await getChatListService({
      cursor: firstId,
      direction: 'before',
      limit: 20,
      type: state.chating.chatType
    }, userStore.deviceId,)
    const { list = [], hasMore } = res.data ?? {}

    if (!list.length) {
      state.hasMore = false
      done()
      return
    }
    state.chating.chatMessageList.unshift(...list)
    state.hasMore = hasMore

    nextTick(() => {
      chatRef.value?.restoreScroll()
      done()
    })
  } catch {
    state.hasMore = false
    done()
    return
  }

}



/* ---------------- 生命周期 ---------------- */
onMounted(() => {
  openWs()
  handleMyMessage()
  document.addEventListener('mousedown', onMouseDown)
  starting.value = false
})

onUnmounted(() => {
  saveSession()
  closeWs()
  document.removeEventListener('mousedown', onMouseDown)
})

/* ---------------- 路由监听 ---------------- */
watch(
  () => route.params.chatType as string,
  (chatType) => {
    if (!chatType) {
      ElMessage.error('没有这个会话喵！')
      return
    }
    const target = chatStore.chatList.find(c => c.route === chatType)
    if (!target) {
      ElMessage.error('加载失败喵！')
      return
    }
    initSession(target.id)
  },
  { immediate: true }
)
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f5f5f5;
}

.el-container {
  height: 100%;
}

.chat-size {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background: #fff;
  overflow: hidden;
  width: 990px;
  height: 650px;
}

.chat-aside {
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e4e7ed;
  padding: 12px 0;
  height: 100%;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #e4e7ed;
  background: #fafafa;

  h1 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
  }

  >div {
    font-size: 12px;
    color: #f56c6c;
  }
}


.chat-main {
  padding: 0 0 0 16px;
  background: #fafafa;
  overflow-y: auto;
}

.chat-footer {
  border-top: 1px solid #e4e7ed;
  padding: 8px 12px;
  background: #fff;
  height: 152px;
}
</style>
