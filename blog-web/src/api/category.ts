// /api/category.ts
import type { Category } from '@/type/article'
import request from '@/utils/request'

//查看文章分类
export const getCategoryListService = () => request.get('/api/admin/article/category/list')

//查看文章分类
export const getCategoryListByPageService = (page: number, pageSize: number) => 
    request.get('/api/admin/article/category/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

//添加文章分类
export const addCategoryService = (category: Category) => request.post('/api/admin/article/category/add', category)

//删除文章分类
export const delCategoryService = (id: any) =>
    request.delete(`/api/admin/article/category/del/${id}`)

//添加文章分类
export const updateCategoryService = (data: Category) => request.post('/api/admin/article/category/update', data)