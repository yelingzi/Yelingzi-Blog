export interface TalkDTO{
    id: number
    title: string,
    content: string,
    imageUrl: string,
    isTop: number,
    state: number
}


export interface Talk{
    title: string,
    content: string,
    imageUrl: string,
    isTop: number,
    userId: number,
    nickname: string,
    userAvatar: string
}

export interface TalkVO{
    id: number,
    title: string,
    content: string,
    imageUrl: string[],
    isTop: number,
    state: number,
    createTime: string,
    userId: number,
    nickname: string,
    userAvatar: string,
    likeCount: number,
    commentCount: number
}

