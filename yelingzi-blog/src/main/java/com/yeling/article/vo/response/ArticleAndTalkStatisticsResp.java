package com.yeling.article.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAndTalkStatisticsResp {

    private Integer id;
    private String type;
    private LocalDateTime createTime;

}
