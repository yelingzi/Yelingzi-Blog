import { defineStore } from 'pinia'
import { reactive, ref } from 'vue'
import type { Chat, UserChat } from '@/types/chatType'
import avatar from '@/assets/images/default.png'

export const useChatStore = defineStore(
  'chatStore',
  () => {

    const userChat = ref<UserChat[]>([])

    /* ------------ 状态 ------------ */
    const chatList = ref<Chat[]>([
      {
        id: '6', route: 'linkme', chatType: 'single', chatMessageList: [], nickname: '叶玲子(管理员)', avatar: avatar, unReadChat: 0,
        lastMessage: {
          messageType: '',
          message: '',
          id: 0,
          userId: 0,
          nickname: '',
          userAvatar: '',
          createTime: '',
        }
      },
      {
        id: 'chatroom', route: 'chatroom', chatType: 'group', chatMessageList: [], nickname: '聊天室', avatar: avatar, unReadChat: 0,
        lastMessage: {
          messageType: '',
          message: '',
          id: 0,
          userId: 0,
          nickname: '',
          userAvatar: '',
          createTime: '',
        }
      }
    ])

    const removeChat = () => {
      chatList.value.splice(0, chatList.value.length)
      chatList.value.push({
        id: '6', route: 'linkme', chatType: 'single', chatMessageList: [], nickname: '叶玲子(管理员)', avatar: avatar, unReadChat: 0,
        lastMessage: {
          messageType: '',
          message: '',
          id: 0,
          userId: 0,
          nickname: '',
          userAvatar: '',
          createTime: '',
        }
      })
      chatList.value.push({
        id: 'chatroom', route: 'chatroom', chatType: 'group', chatMessageList: [], nickname: '聊天室', avatar: avatar, unReadChat: 0,
        lastMessage: {
          messageType: '',
          message: '',
          id: 0,
          userId: 0,
          nickname: '',
          userAvatar: '',
          createTime: '',
        }
      })
    }

    return {
      chatList,
      removeChat
    }
  },
  { persist: true }
)
