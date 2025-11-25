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

    const addChatByWsMsg = (msg: { receiver: string, messageType: 'single' | 'group',  message: any }): Chat => {
      const newChat: Chat = {
        id: msg.receiver,
        route: msg.receiver,
        nickname: msg.message.nickname,
        chatType: msg.messageType,
        avatar: msg.message.userAvatar,
        lastMessage: msg.message,
        unReadChat: 0,
        chatMessageList: [msg.message],
      }
      chatList.value.unshift(newChat)
      return newChat
    } 

    return {
      chatList,
      removeUnReadChat,
      addChatByWsMsg
    }
  },
  { persist: true }
)
