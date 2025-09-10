// /api/message.ts
import type { MessageDTO } from '@/type/message'
import request from '@/utils/request'

//查看留言
export const getMessageListService = () => request.get('/api/message/list')

//查看一页留言
export const getMessageListByPageService = (page: number, pageSize: number) => 
    request.get('/api/admin/interact/message/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

//添加留言
export const addMessageService = (data: MessageDTO) => request.post('/api/message/add', data)

// 删除留言
export const delMessageService = (id: number) =>
    request.post('/api/admin/interact/message/del', { id });

// 通过留言
export const updateMessageService = (id: number) =>
    request.post('/api/admin/interact/message/pass', { id });