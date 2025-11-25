package com.yeling.yelingziblog.article.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSearchReq {

    private String title;
    private List<LocalDateTime> date;
    private Integer state;
    private String nickname;
    private String category;
    private String tag;
    private Integer isTop;
    private Integer isOriginal;
    private Integer page;
    private Integer pageSize;

}
