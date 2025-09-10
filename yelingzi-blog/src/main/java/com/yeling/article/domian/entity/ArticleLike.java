package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLike {

    private Integer userId;
    private Integer articleId;
    private LocalDateTime likeTime; //创建时间
}
