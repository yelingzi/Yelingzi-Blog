package com.yeling.yelingziblog.article.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatList {

    private String title;
    private Integer readCount;
    private Integer likeCount;
    private Integer commentCount;

}
