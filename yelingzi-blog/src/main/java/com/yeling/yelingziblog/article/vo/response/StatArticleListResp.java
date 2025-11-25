package com.yeling.yelingziblog.article.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatArticleListResp {

    private String articleName;

    private Integer count;

}
