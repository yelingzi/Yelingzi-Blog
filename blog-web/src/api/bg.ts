import request from '@/utils/request'

//查看一页展示的背景图片
export const getBgListByPageService = (page: number, pageSize: number) =>
    request.get('/api/admin/background/page', {
        params: {
            page: page,
            pageSize: pageSize
        },
    })

//查看一页未展示的背景图片
export const getBgDelListByPageService = (page: number, pageSize: number) =>
    request.get('/api/admin/background/del/page', {
        params: {
            page: page,
            pageSize: pageSize
        },
    })

// 删除背景图片
export const delBgCommentService = (id: number) =>
    request.post('/api/admin/background/del', { id });

// 展示背景图片
export const updateBgCommentService = (id: number) =>
    request.post('/api/admin/background/show', { id });

//添加背景图片
export const uploadBgImageService = (data: FormData) => request.post('/api/admin/background/upload', data)