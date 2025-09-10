package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentLike {

    private Integer id;          // 自增主键
    private Integer commentId;  // 评论ID
    private Integer articleId;      // 文章ID
    private Integer userId;      // 用户ID

}
