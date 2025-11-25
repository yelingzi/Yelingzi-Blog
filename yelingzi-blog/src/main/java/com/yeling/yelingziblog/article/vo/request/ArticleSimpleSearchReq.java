package com.yeling.yelingziblog.article.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSimpleSearchReq {

    private String title;
    private List<LocalDateTime> date;
    private Integer state;
    private Integer page;
    private Integer pageSize;
}
