import type { ArticleCommentForm, TalkCommentForm } from '@/types/comment'
import request from '@/utils/request'

export const getArticleCommentListService = (articleId: number) =>
  request.get('/api/article/comment/list', {
    params: {
      articleId: articleId,
    },
  })

export const getTalkCommentListService = (talkId: number) =>
  request.get('/api/talk/comment/list', {
    params: {
      talkId: talkId,
    },
  })

export const getArticleCommentLikeListService = (articleId: number) =>
  request.get('/api/user/article/comment/like/list', {
    params: {
      articleId: articleId,
    },
  })

export const addArticleCommentService = (row: ArticleCommentForm) => {
  return request.post('/api/user/article/comment/add', row)
}

export const addArticleCommentLikeService = (commentId: number, articleId: number) =>
  request.get('/api/user/article/comment/like/add', {
    params: {
      commentId: commentId,
      articleId: articleId,
    },
  })

export const delArticleCommentLikeService = (likeId: number) =>
  request.get('/api/user/article/comment/like/cancel', {
    params: {
      id: likeId,
    },
  })

export const getTalkCommentLikeListService = (talkId: number) =>
  request.get('/api/user/talk/comment/like/list', {
    params: {
      talkId: talkId,
    },
  })

export const addTalkCommentService = (row: TalkCommentForm) => {
  return request.post('/api/user/talk/comment/add', row)
}

export const addTalkCommentLikeService = (commentId: number, talkId: number) =>
  request.get('/api/user/talk/comment/like/add', {
    params: {
      commentId: commentId,
      talkId: talkId,
    },
  })

export const delTalkCommentLikeService = (likeId: number) =>
  request.get('/api/user/talk/comment/like/cancel', {
    params: {
      id: likeId,
    },
  })
