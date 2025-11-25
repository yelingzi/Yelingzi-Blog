import request from '@/utils/request'




export const getTalkListByPageService = (page: number, pageSize: number) =>
  request.get('/api/talk/page', {
    params: {
      page: page,
      pageSize: pageSize
    },
  })

export const getTalkByIdService = (id: number) => request.get(`/api/talk/${id}`)

export const getTopTalkListService = () => request.get('/api/talk/top')

export const getTalkLikeService = (id: number) => request.get(
  '/api/user/talk/like', {
  params: {
    id: id
  },
})

export const addTalkLikeService = (id: number) => request.get(`/api/user/talk/like/${id}`)

export const delTalkLikeService = (id: number) => request.get(`/api/user/talk/unlike/${id}`)
