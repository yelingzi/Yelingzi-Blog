package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleInfo {
    private String name;//发布者名称
    private String id;//文章id
    private String title;//文章标题
    private String content;//文章内容
    private String pictureCover;//文章封面
    private Integer star;//文章点赞
    private Integer isLiked;
    private LocalDateTime updateTime;//更新时间
    private Integer views;
    private Integer articleComment;
    private Integer userId;
}
