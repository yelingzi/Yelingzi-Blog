import request from '@/utils/request'

//查看留言
export const getMessageListService = () => request.get('/api/message/list')

//添加留言
export const addMessageService = (data: { content: string }) => request.post('/api/message/add', data)
