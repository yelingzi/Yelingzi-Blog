package com.yeling.article.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagFindArticleResp {

    private Integer id;

    private String title;//文章标题

    private String content;//文章内容

    private String articleCover;//文章封面

    private CategoryResp category;//文章分类

    private LocalDateTime createTime; //创建时间

    private List<TagResp> tagList;


}
