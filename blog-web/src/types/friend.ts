export interface Friend {
  cover: string
  title: string
  introduction: string
  recommendStatus: boolean
  createTime: string
  url: string
}

export interface FriendGroup {
  [category: string]: Friend[]
}

export interface FriendDTO {
  cover: string
  title: string
  introduction: string
  url: string
}
