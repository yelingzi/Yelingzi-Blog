export interface ArticleDTO {
    id: number 
    title: string,
    content: string,
    category: number,
    articleCover: string,
    tagList: string,
    state: number,
    isOriginal: number,
    originalUrl: string,
    isTop: number
}

export interface ArticleVO {
    id: number
    title: string,
    content: string,
    category: Category,
    articleCover: string,
    tagList: Tags[],
    state: number,
    isOriginal: number,
    originalUrl: string,
    isTop: number,
    starCount: number,
    readCount: number,
    likeCount: number,
    commentCount: number,
    nickname: number,
    userId: number,
    userAvatar: string,
    createTime: string,
    updateTime: string
}

export interface ArticleForm {
    title: string,
    category: string,
    tag: Tags[],
    articleCover: string,
}

export interface Category {
    id: number,
    categoryName: string
}

export interface Tags {
    id: number,
    tagName: string
}

export interface CheckTags {
    id: number,
    tagName: string,
    check: boolean
}

export interface CategoryList {
    id: number,
    categoryName: string,
    userId: number,
    nickname: string,
    createTime: string,
    articleCount: number,
    isDel: number
}

export interface TagList {
    id: number,
    tagName: string,
    userId: number,
    nickname: string,
    createTime: string,
    articleCount: number,
    isDel: number
}

export interface Article {
    id: number;
    nickname: string;
    title: string;
    content: string;
    articleCover: string;
    category: Category;
    state: number;
    createTime: string;
    userId: number;
    userAvatar: string;
    tagList: Tag[];
    isOriginal: number;
    originalUrl: string;
    isTop: number
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