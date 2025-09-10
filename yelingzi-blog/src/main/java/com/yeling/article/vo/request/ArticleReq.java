package com.yeling.article.vo.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleReq {

    private Integer id;

    private String nickname;//发布者名称

    @NotBlank(message = "文章标题不能为空")
    private String title;//文章标题

    @NotBlank(message = "文章内容不能为空")
    private String content;//文章内容

    @NotBlank(message = "文章内容不能为空")
    private String articleCover;//文章封面

    @NotNull(message = "文章分类不能为空")
    @Min(value = 1, message = "文章分类ID必须大于 0")
    private Integer category;//文章分类


    private Integer state;//是否发布

    private Integer userId;

    private String userAvatar;

    @NotBlank(message = "文章标签不能为空")
    private String tagList;

    private Integer isOriginal;

    private String originalUrl;

    private Integer isTop;
}
