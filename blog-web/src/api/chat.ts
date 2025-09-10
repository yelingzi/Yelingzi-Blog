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
export const sendMessageService = (parmas: { type: string, message: string }, device: string) => {
  return request.post('/api/chat/send', parmas, {
    headers: { 'x-host': device }
  })
}

//发送图片消息
export const sendSingleImageService = (data: FormData, device: string) => {
  return request.post('/api/chat/send/image', data, {
    headers: { 'x-host': device }
  })
}
