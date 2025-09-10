import type { TalkDTO } from '@/type/talk'
import request from '@/utils/request'


//查看说说
export const getTalkListService = () => request.get('/api/user/talk/list')

//查看说说
export const getTalkListByPageService = (page: number, pageSize: number) => 
    request.get('/api/admin/talk/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

//添加说说
export const addTalkService = (category: TalkDTO) => request.post('/api/admin/talk/add', category)

//编辑说说
export const editTalkService = (category: TalkDTO) => request.post('/api/admin/talk/add', category)

export const getTalkByIdService = (id: number) => request.get(`/api/talk/${id}`)

//删除说说
export const delTalkService = (id: any) =>
    request.delete(`/api/admin/talk/del/${id}`)

//复原
export const regainTalkService = (id: number) =>
  request.post('/api/admin/talk/regain', { id });

//更新说说
export const updateTalkService = (data: TalkDTO) => request.post('/api/admin/talk/update', data)

//说说图片
export const uploadTalkImageService = (data: FormData) => request.post('/api/admin/talk/upload/image', data)

// 置顶文章
export const updateTalkTopService = (id: number, isTop: number) =>
  request.post('/api/admin/talk/top', { id, isTop });
