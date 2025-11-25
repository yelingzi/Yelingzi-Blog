package com.yeling.yelingziblog.article.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleLike {

    private Integer id;
    private Integer userId;
    private Integer articleId;
    private LocalDateTime likeTime; //创建时间
}
