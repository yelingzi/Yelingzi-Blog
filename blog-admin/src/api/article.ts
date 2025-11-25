import type { ArticleDTO } from '@/type/article'
import request from '@/utils/request'

//添加文章
export const articleAddService = (data: ArticleDTO) => request.post('/api/admin/article', data)

//添加文章封面
export const uploadArticleCoverService = (data: FormData) => request.post('/api/admin/article/upload/cover', data)

//添加文章图片
export const uploadArticleImageService = (data: FormData) => request.post('/api/admin/article/upload/image', data)

//根据id查看文章
export const getArticleByIdService = (num: number) => request.get(`/api/article/${num}`)

//查看所有文章
export const getArticleService = () => request.get('/api/article')

// 删除文章
export const delArticleService = (id: number) =>
  request.post('/api/admin/article/del', { id });

export const regainArticleService = (id: number) =>
  request.post('/api/admin/article/regain', { id });



// 置顶文章
export const updateArticleTopService = (id: number, isTop: number) =>
  request.post('/api/admin/article/top', { id, isTop });

//查看文章
export const getArticleListService = () => request.get('/api/admin/article/list')

//查看文章
export const getArticleListByPageService = (page: number, pageSize: number) =>
  request.get('/api/admin/article/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })


//更新文章
export const updateArticleService = (data: ArticleDTO) => request.post('/api/admin/article/update', data)

//根据id查看文章点赞列表
export const getArticleLikeListByPageService = (id: number, page: number, pageSize: number) =>
  request.get('/api/admin/article/id/like/page', {
    params: {
      articleId: id,
      page: page,
      pageSize: pageSize
    },
  })

//简单搜索文章
export const simpleSearchArticleService =
  (data: { date: string[] | Date[]; title: string; state: string; page: number; pageSize: number }) =>
    request.post('/api/admin/article/search/simple', data)

  //简单搜索文章
export const searchArticleService =
  (data: any) =>
    request.post('/api/admin/article/search/list', data)

//搜索文章
export const searchArticleTitleListService = (search: string) =>
  request.get('/api/admin/article/search/title', {
    params: {
      search: search,
    },
  })

export const searchUserListService = (search: string) =>
  request.get('/api/admin/user/search/nickname', {
    params: {
      search: search,
    },
  })

export const searchArticleTagListService = (search: string) =>
  request.get('/api/admin/article/search/tag', {
    params: {
      search: search,
    },
  })

export const searchArticleCategoryListService = (search: string) =>
  request.get('/api/admin/article/search/category', {
    params: {
      search: search,
    },
  })