/**
 * 说说
 */
export interface Talk {
  //说说id
  id: number
  //昵称
  nickname: string
  //用户Id
  userId: number
  //头像
  userAvatar: string
  //标题
  title: string
  //说说内容
  content: string
  //图片列表
  imageUrl: string[]
  //是否置顶 (0否 1是)
  isTop: number
  //点赞量
  likeCount: number
  //评论量
  commentCount: number
  //创建时间
  createTime: string
}

/**
 * 说说
 */
export interface SimpleTalk {
  //说说id
  id: number
  //标题
  title: string
  //说说内容
  content: string
  //图片列表
}
