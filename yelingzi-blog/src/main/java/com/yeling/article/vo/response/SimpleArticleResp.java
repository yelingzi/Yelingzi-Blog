package com.yeling.article.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleArticleResp {

    private Integer id;

    private String title;//文章标题

    private String content;//文章内容

    private String category;//文章分类

    private LocalDateTime createTime; //创建时间



}
