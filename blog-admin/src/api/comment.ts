import request from '@/utils/request'

//查看一页文章评论
export const getArticleCommentListByPageService = (page: number, pageSize: number) =>
    request.get('/api/admin/comment/article/page', {
        params: {
            page: page,
            pageSize: pageSize
        },
    })

// 删除文章评论
export const delArticleCommentService = (id: number) =>
    request.post('/api/admin/comment/article/del', { id });

// 更新文章评论
export const updateArticleCommentService = (id: number) =>
    request.post('/api/admin/comment/article/pass', { id });

//查看一页说说评论
export const getTalkCommentListByPageService = (page: number, pageSize: number) =>
    request.get('/api/admin/comment/talk/page', {
        params: {
            page: page,
            pageSize: pageSize
        },
    })

// 删除说说评论
export const delTalkCommentService = (id: number) =>
    request.post('/api/admin/comment/talk/del', { id });

// 更新说说评论
export const updateTalkCommentService = (id: number) =>
    request.post('/api/admin/comment/talk/pass', { id });