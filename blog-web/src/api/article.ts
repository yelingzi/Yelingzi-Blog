import request from '@/utils/request'

//查看文章
export const getArticleListService = () => request.get('/api/article/list')

//查看文章
export const getArticleListByPageService = (page: number, pageSize: number) =>
  request.get('/api/article/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

export const getArticleByIdService = (id: number) => request.get(`/api/article/${id}`)

export const getArticleListFromCategoryService = () => request.get('/api/article/category/list')

export const getTagListService = () => request.get('/api/article/tag/list')

export const getArticleListByTagService = (id: number) => request.get(
  '/api/article/tag', {
  params: {
    tagId: id
  },
})

export const searchArticleListService = (search: string) => request.get(
  '/api/article/search', {
  params: {
    search: search
  },
})

export const getArticleLikeService = (id: number) => request.get(
  '/api/user/article/like', {
  params: {
    id: id
  },
})

export const addArticleLikeService = (id: number) => request.get(`/api/user/article/like/${id}`)

export const delArticleLikeService = (id: number) => request.get(`/api/user/article/unlike/${id}`)

export const getRecommendArticleListService = () => request.get('/api/article/recommend/list')
