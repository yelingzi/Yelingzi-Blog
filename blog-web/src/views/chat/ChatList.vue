<template>
  <div v-for="chat in chatStore.chatList" class="chat-list" @click="pushChating(chat)" :key="chat.id"
    :class="{ active: chat.id === props.currentChatId }">
    <el-avatar :src="chat.avatar"></el-avatar>
    <div class="chat-content">
      <div class="chat-info">
        <div class="chat-name">{{ chat.nickname }}</div>
        <div v-if="chat.lastMessage.createTime !== ''" class="chat-time">{{
          formatChatListDisplayTime(chat.lastMessage.createTime) }}</div>
      </div>

      <div class="message-preview">
        <div class="message-content">
          <template v-if="chat.lastMessage">
            <span v-if="chat.lastMessage.nickname !== '' && chat.chatType == 'group'" class="sender-name">{{
              chat.lastMessage.nickname }}:</span>
            <span v-if="chat.lastMessage.messageType === 'text'">{{ chat.lastMessage.message }}</span>
            <span v-else-if="chat.lastMessage.messageType === 'image'" class="image-msg"></span>
            <span v-else-if="chat.lastMessage.messageType === 'file'" class="file-msg"></span>
          </template>
        </div>

        <div v-if="chat.unReadChat > 0 && chat.id !== props.currentChatId" class="unread-badge">
          {{ chat.unReadChat < 100 ? chat.unReadChat : '99+' }} </div>
        </div>
      </div>
    </div>
</template>

<script lang="ts" setup>
import type { Chat } from '@/types/chatType';
import { useChatStore } from '@/stores/modules/chat'
import { useRouter } from 'vue-router';
import { formatChatListDisplayTime } from '@/utils/common'

const props = defineProps({
  currentChatId: {
    type: String,
    required: true
  }
})
const router = useRouter()
const chatStore = useChatStore()

const pushChating = (chat: Chat) => {
  router.push({ name: 'chat', params: { chatType: chat.route } })
}
</script>

<style lang="scss" scoped>
.chat-list {
  padding: 8px 12px;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;

  &:hover {
    background-color: #f2f6fc;
  }

  .el-avatar {
    margin-right: 8px;
    flex-shrink: 0;
  }

  div {
    font-size: 14px;
    color: #303133;
  }

  &:hover {
    background-color: #f3f3f3;
  }

  &.active {
    background-color: var(--el-color-primary-light-9); // 主题色浅色背景
    color: var(--el-color-primary);
  }

}


.chat-content {
  flex: 1;
  min-width: 0;
  /* 关键设置 */
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.chat-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.chat-name {
  font-size: 17px;
  font-weight: 600;
  color: #2d3748;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-time {
  font-size: 12px;
  color: #718096;
  flex-shrink: 0;
}

.message-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.message-content {
  font-size: 14px;
  color: #718096;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
  min-width: 0;
}

.sender-name {
  color: #4a5568;
  font-weight: 500;
}

.unread-badge {
  background: #ff6b6b;
  color: white;
  border-radius: 10px;
  height: 20px;
  min-width: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
}

.image-msg::after {
  content: "【图片】";
  color: #4299e1;
}

.file-msg::after {
  content: "【文件】";
  color: #48bb78;
}
</style>
