import request from '@/utils/request'


//查看统计数据
export const getStatisticsService = () => request.get('/api/admin/home/statistics')

//查看发布内容统计数据
export const getArtAndTalkStatisticsService = () => request.get('/api/admin/home/update')

//查看最近7天浏览量
export const getViewStatisticsService = () => request.get('/api/admin/home/view')

//查看最近访客
export const getViewInfoListService = () => request.get('/api/admin/home/view/list')