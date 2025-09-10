export interface ArticleList {
  /**
   * 文章id
   */
  id: number
  /**
   * 文章缩略图
   */
  articleCover: string
  /**
   * 文章标题
   */
  title: string
  /**
   * 文章摘要
   */
  articleDesc: string
  /**
   * 文章分类
   */
  articleClassify: Tag
  /**
   * 文章标签
   */
  tagList: Tag[]
  /**
   * 是否置顶 (0否 1是)
   */
  isTop: number
  /**
   * 发表时间
   */
  createTime: string
  /**
   * 更新时间
   */
  updateTime: string
}

export interface Article {
  id: number;
  nickname: string;
  title: string;
  content: string;
  articleCover: string;
  category: Category;
  starCount: number;
  state: number;
  createTime: string;
  updateTime: string;
  readCount: number;
  commentCount: number;
  userId: number;
  userAvatar: string;
  tagList: Tag[];
  likeCount: number;
  isOriginal: number;
  originalUrl: string;
  isTop: number;
  isLike: boolean
  isStar: boolean
}

export interface ArticleRecommend {
  id: number
  articleCover: string
  title: string
  createTime: string
}
export interface Tag {
  /**
   * 标签id
   */
  id: number
  /**
   * 标签名
   */
  tagName: string
}

export interface Category {
  id: number;
  categoryName: string;
}

export interface Archives {
  id: number
  title: string
  createTime: string
}
