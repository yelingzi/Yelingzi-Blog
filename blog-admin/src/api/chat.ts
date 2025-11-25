import request from '@/utils/request'

//聊天列表
export const getChatListService = (parmas: { cursor: number, direction: string, limit: number, type: string }, device: string) => {
  return request.post('/api/chat/list', parmas, {
    headers: { 'x-host': device },
  })
}


//获取新消息总数
export const getNewChatCountBySingleService = (parmas: { device: string, id: number }) => {
  return request.get('/api/chat/single/new', {
    headers: { 'x-host': parmas.device },
    params: { cursor: parmas.id }
  })
}

export const getNewChatCountByGroupService = (parmas: { device: string, id: number }) => {
  return request.get('/api/chat/group/new', {
    headers: { 'x-host': parmas.device },
    params: { cursor: parmas.id }
  })
}

//发送文字消息
export const sendMessageService = (parmas: { chatType: string, message: string, toUser: string }) => {
  return request.post('/api/admin/chat/send/single', parmas)
}

//发送图片消息
export const sendImageService = (parmas: FormData) => {
  return request.post('/api/admin/chat/send/image', parmas)
}

//发送表情消息
export const sendEmojiService = (parmas: { chatType: string, message: string, toUser: string }) => {
  return request.post('/api/admin/chat/send/emoji', parmas)
}

