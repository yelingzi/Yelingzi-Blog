package com.yeling.article.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResp {
    private Integer id;

    private String nickname;//发布者名称

    private String title;//文章标题

    private String content;//文章内容

    private String articleCover;//文章封面

    private CategoryResp category;//文章分类

    private Integer starCount;

    private Integer state;

    private LocalDateTime createTime; //创建时间

    private LocalDateTime updateTime;//更新时间

    private Integer readCount; //阅读量

    private Integer commentCount;

    private Integer userId;

    private String userAvatar;

    private List<TagResp> tagList;

    private Integer likeCount;

    private Integer isOriginal;

    private String originalUrl;

    private Integer isTop;
}
