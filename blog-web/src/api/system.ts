import request from '@/utils/request'

//查看可导出的表列表
export const getTablesListService = () => request.get('/api/admin/system/export/tables')

export const exportTableService = (data: { tableName: string}) => {
  return request.post('/api/admin/system/export/excel', data)
} 