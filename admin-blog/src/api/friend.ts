import type { FriendDTO } from '@/type/friend';
import request from '@/utils/request'


//查看一页友链
export const getFriendListByPageService = (page: number, pageSize: number) => 
    request.get('/api/admin/interact/friend/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

//添加友链
export const addFriendService = (data: FriendDTO) => request.post('/api/friend/add', data)

// 删除友链
export const delFriendService = (id: number) =>
    request.post('/api/admin/interact/friend/del', { id });

// 通过友链
export const updateFriendService = (id: number) =>
    request.post('/api/admin/interact/friend/pass', { id });