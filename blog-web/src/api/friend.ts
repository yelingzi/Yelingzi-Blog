import type { FriendDTO } from '@/types/friend'
import request from '@/utils/request'



export const getFriendService = () => request.get('/api/friend/list')

//添加友链
export const addFriendService = (data: FriendDTO) => request.post('/api/friend/add', data)

//添加友链
export const addUserFriendService = (data: FriendDTO) => request.post('/api/user/friend/add', data)
