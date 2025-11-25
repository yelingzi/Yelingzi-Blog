export interface HomeStatistics{
    articleCount: number
    userCount:number
    viewCount:number
    messageCount:number
    commentCount:number
    tagCount:number
    talkCount:number
    categoryCount:number
}

export interface ArtAndTalkList {
    id: number
    type: '文章' | '说说'
    createTime: string 
}

export interface ViewData{
    id: number
    viewCount: number
    createTime: string
}
export interface ViewInfo{
    ip: string
    city: string
    createTime: string
    nickname: string
}