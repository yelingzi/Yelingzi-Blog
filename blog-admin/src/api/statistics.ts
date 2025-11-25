import request from '@/utils/request'


//查看统计数据
export const getStatisticsService = () => request.get('/api/admin/home/statistics')

//查看发布内容统计数据
export const getArtAndTalkStatisticsService = () => request.get('/api/admin/home/update')

//查看最近7天浏览量
export const getViewStatisticsService = () => request.get('/api/admin/home/view')

//查看最近访客
export const getViewInfoListService = () => request.get('/api/admin/home/view/list')

//查看点赞数排行榜
export const getLikeCountRankService = () => request.get('/api/article/like/rank')

//查看浏览量排行榜
export const getViewCountRankService = () => request.get('/api/article/view/rank')

//查看评论数排行榜
export const getCommentCountRankService = () => request.get('/api/article/comment/rank')