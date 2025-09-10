<!-- 群聊信息展示 -->
<template>
  <div class="chat-wrapper" ref="chatWrapperRef">
    <!-- 触发器 -->
    <div ref="triggerRef" class="load-trigger"></div>
    <div v-if="loading" class="loading-tip">加载中...</div>
    <div v-if="!props.hasMore" class="no-more-tip">没有更多消息了</div>

    <div class="chat-container">
      <div class="chat-record" v-for="chat in displayList" :key="chat.id">
        <!-- 时间居中显示，小于3分钟不重复显示 -->
        <div class="chat-record-time" v-if="chat.showTime">{{ chat.formattedTime }}</div>
        <!-- 自己消息，显示在右边 -->
        <div class="chat-record-right" v-if="chat.userId === userInfo.userId || chat.nickname === userInfo.nickname">
          <div class="chat-record-content">
            <div class="chat-record-right-nickname">{{ chat.nickname }}</div>
            <div class="chat-record-text user-select" v-if="chat.messageType === 'text'">{{ chat.message }}</div>
          </div>

          <el-avatar class="chat-record-avatar" :src="chat.userAvatar"></el-avatar>
        </div>
        <!-- 其他人消息，显示在左边 -->
        <div class="chat-record-left" v-else>
          <el-avatar class="chat-record-avatar" :src="chat.userAvatar"></el-avatar>
          <div class="chat-record-content">
            <div class="chat-record-nickname">{{ chat.nickname }}</div>
            <div class="chat-record-text user-select" v-if="chat.messageType === 'text'">{{ chat.message }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, nextTick, onMounted, onUnmounted, ref, watch, type PropType } from 'vue'
import type { ChatMessage } from '@/type/chatType'
import { useUserStore } from '@/stores'
import { formatChatDisplayTime } from '@/utils/commom'

const userState = useUserStore()
const userInfo = userState.userInfo
const chatWrapperRef = ref<HTMLElement>()
const props = defineProps({
  chatRecordList: {
    type: Array as PropType<ChatMessage[]>,
    required: true
  },
  hasMore: {
    type: Boolean,
    required: true
  }
})

/* 本地状态 */
const loading = ref(false)
let autoScrollEnabled = true

/* 父组件通过 expose 把下面两个方法暴露给子组件 */
const emit = defineEmits(['load-more'])

/* 触发器元素 */
const triggerRef = ref<HTMLElement>()
let observer: IntersectionObserver

/* 处理后的聊天列表，增加 showTime 字段 */
const displayList = computed(() => {
  const list = props.chatRecordList
  return list.map((cur, idx) => {
    // 添加格式化后的时间字段
    const formattedTime = formatChatDisplayTime(cur.createTime);

    if (idx === 0) {
      // 第一条一定显示
      return { ...cur, showTime: true, formattedTime }
    }
    const prev = list[idx - 1]
    const diff = new Date(cur.createTime).getTime() - new Date(prev.createTime).getTime()
    const showTime = diff >= 3 * 60 * 1000 // 3 分钟及以上才显示
    return {
      ...cur,
      showTime,
      formattedTime
    }
  })
})

/* 真正去加载 */
const loadMore = async () => {
  if (!props.hasMore || loading.value) return
  loading.value = true
  autoScrollEnabled = false
  lockScroll()

  emit('load-more', () => {
    loading.value = false
    restoreScroll()
    autoScrollEnabled = true
  })
}

/* 滚动到底部 */
const scrollToBottom = () => {
  if (chatWrapperRef.value) {
    chatWrapperRef.value.scrollTop = chatWrapperRef.value.scrollHeight
  }
}

/* 把当前滚动高度记录下来，方便父组件插入数据后恢复位置 */
let lastScrollHeight = 0
const lockScroll = () => {
  if (!chatWrapperRef.value) return
  lastScrollHeight = chatWrapperRef.value.scrollHeight
}

const restoreScroll = () => {
  nextTick(() => {
    if (!chatWrapperRef.value) return
    const newHeight = chatWrapperRef.value.scrollHeight
    chatWrapperRef.value.scrollTop = chatWrapperRef.value.scrollHeight - lastScrollHeight
  })
}

onMounted(() => {
  if (!chatWrapperRef.value) return

  observer = new IntersectionObserver(
    entries => {
      if (entries[0].isIntersecting && !loading.value && props.hasMore) {
        loadMore()
      }
    },
    {
      root: null,
      rootMargin: '500px 0px 0px 0px',
      threshold: 0.1
    }
  )

  if (triggerRef.value) {
    observer.observe(triggerRef.value)
  }

  // 初始滚动到底部
  nextTick(() => {
    scrollToBottom()
  })
})

onUnmounted(() => observer?.disconnect())

watch(
  () => props.chatRecordList.length,
  async () => {
    await nextTick()
    if (!chatWrapperRef.value) return

    const { scrollTop, scrollHeight, clientHeight } = chatWrapperRef.value
    const distanceToBottom = scrollHeight - scrollTop - clientHeight

    if (autoScrollEnabled && distanceToBottom < 2000) {
      scrollToBottom()
    }
  }
)

/* 导出给父组件调用 */
defineExpose({ lockScroll, restoreScroll, scrollToBottom })
</script>

<style lang="scss" scoped>
.chat-wrapper {
  height: 100%;
  overflow-y: auto;
  display: flex;
  flex-direction: column;

  overflow-y: auto !important;
  scrollbar-width: auto;
  -ms-overflow-style: auto;
  scrollbar-width: auto;
  scrollbar-color: #c4c4c4 transparent;
}

.chat-record-right {
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  gap: 8px;
  margin: 18px 0;

  .chat-record-text {
    background: #95ec69;
    color: #000;
    order: -1;
  }
}

.chat-record-left {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin: 18px 0;

  .chat-record-text {
    background: #f2f2f2;
    color: #333;
    order: -1;
  }
}



.chat-record {
  margin-bottom: 12px;
  width: 100%;
}

.chat-record-time {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin: 8px 0;
}

.chat-record-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.chat-record-content {
  max-width: 60%;
}

.chat-record-right-nickname {
  display: flex;
  justify-content: flex-end;
}

.chat-record-nickname {
  margin-bottom: 4px;
}

.chat-record-text {
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 14px;
  word-break: break-word;

}

.no-more-tip {
  display: flex;
  justify-content: center;
  padding: 5px 0 10px 0;
}

.input-textarea::-webkit-scrollbar {
  width: 20px;
}

.input-textarea::-webkit-scrollbar-thumb {
  background: #c4c4c4;
  border-radius: 5px;
}

.input-textarea::-webkit-scrollbar-track {
  background: transparent;
}
</style>
