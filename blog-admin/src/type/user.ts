export interface UserInfo {
    id: number,
    email: string,
    nickname: string,
    userAvatar: string
    isLogin: boolean
}
export interface UserState {
    userInfo: UserInfo
    token: string,
    isLogin: boolean,
}

export interface MenuList {
    id: number,
    parentId: number,
    menuName: string,
    path: string,
    icon: string,
    sortOrder: number,
    component: string,
    menuType: number
    children: MenuList[]
}