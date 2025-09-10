/**
 * 评论
 */
export interface Comment {
  //评论id
  id: number
  //评论用户id
  userId: number
  //昵称
  userNickname: string
  //头像
  userAvatar: string
  //评论内容
  content: string
  //父级评论id
  parentId: number
  //被评论用户id
  toId: number
  //被评论用户昵称
  toNickname: string
  //点赞数
  likeCount: number
  //回复量
  replyCount: number
  //回复列表
  children: Comment[]
  //评论时间
  createTime: string
  isLike: boolean
  isPeply: boolean
}

/**
 * 文章评论表单
 */
export interface ArticleCommentForm {

  //父评论id
  parentId: number
  //被回复用户id
  toId: number
  //被回复用户id
  toNickname: string
  //评论内容
  content: string
  articleId: number
}
/**
 * 说说评论表单
 */
export interface TalkCommentForm {

  //父评论id
  parentId: number
  //被回复用户id
  toId: number
  //被回复用户id
  toNickname: string
  //评论内容
  content: string
  talkId: number
}


export interface LikeList {
  commentId: number
  id: number
}
