import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { Chat } from '@/type/chatType'

export const useChatStore = defineStore(
  'chatStore',
  () => {
    /* ------------ 状态 ------------ */
    const chatList = ref<Chat[]>([])


    const removeUnReadChat = (id: string) => {
      chatList.value.forEach((chat: { id: string; unReadChat: number }) => {
        if (chat.id == id) {
          chat.unReadChat = 0
        }
      })
    }


    return {
      chatList,
      removeUnReadChat
    }
  },
  { persist: true }
)
