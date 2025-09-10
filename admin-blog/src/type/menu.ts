export interface MenuDTO{
    id: number,
    menuName: string,
    path: string,
    icon: string,
    sortOrder: number,
    pathPattern: string,
    component: string,
    menuType: number,
    parentId: number,
}

export interface MenuVO{
    id: number,
    menuName: string,
    path: string,
    icon: string,
    sortOrder: number,
    pathPattern: string,
    component: string,
    menuType: number,
    parentId: number,
    createTime: string,
    isDel: number,
    children: MenuVO[]
}