// /api/tag.ts
import type { Tags } from '@/type/article'
import request from '@/utils/request'

//查看文章标签
export const getTagListService = () => request.get('/api/admin/article/tag/list')

//查看一页文章标签
export const getTagListByPageService = (page: number, pageSize: number) => 
    request.get('/api/admin/article/tag/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

//添加文章标签
export const addTagService = (data: Tags) => request.post('/api/admin/article/tag/add', data)

//删除文章
export const delTagService = (id: any) =>
    request.delete(`/api/admin/article/tag/del/${id}`)

//添加文章标签
export const updateTagService = (data: Tags) => request.post('/api/admin/article/tag/update', data)