export interface MessageVO{
    id: number
    content:String
    nickname:String
    ip:String
    ipLocation:String
    state:number
    userAvatar:String
    userId:number
}

export interface MessageDTO{
    content:String
}