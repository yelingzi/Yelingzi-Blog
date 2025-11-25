import type { MenuDTO } from '@/type/menu';
import request from '@/utils/request'


//查看菜单
export const getMenuListService = (id: number) =>
    request.get('/api/admin/authority/menu/list', {
        params: {
            id: id
        },
    })

//添加菜单
export const addMenuService = (data: MenuDTO) => request.post('/api/admin/authority/menu/add', data)

//添加菜单
export const updateMenuService = (data: MenuDTO) => request.post('/api/admin/authority/menu/update', data)

// 删除菜单
export const delMenuService = (id: number) =>
    request.post('/api/admin/authority/menu/del', { id });

// 恢复菜单
export const renewMenuService = (id: number) =>
    request.post('/api/admin/authority/menu/renew', { id });
