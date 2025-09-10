package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    private Integer id;

    private String nickname;//发布者名称

    private String title;//文章标题

    private String content;//文章内容

    private String articleCover;//文章封面

    private String category;//文章分类

    private String articleUrl;//文章地址

    private Integer starCount;

    private Integer state;

    private LocalDateTime createTime; //创建时间

    private LocalDateTime updateTime;//更新时间

    private Integer readCount; //阅读量

    private Integer commentCount;

    private Integer userId;

    private String userAvatar;

    private String tagList;

    private Integer likeCount;

    private Integer isOriginal;

    private String originalUrl;

    private Integer isTop;

    private String brief;

}
