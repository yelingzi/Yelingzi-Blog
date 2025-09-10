export interface ArticleComment {
    id: number
    articleId: number
    userId: number
    userNickname: string
    userAvatar: string
    content: string
    parentId: number
    toId: number
    toNickname: string
    replyCount: number
    createTime: string
    state: string
}

export interface TalkComment {
    id: number
    talkId: number
    userId: number
    userNickname: string
    userAvatar: string
    content: string
    parentId: number
    toId: number
    toNickname: string
    replyCount: number
    createTime: string
    state: string
}