package com.yeling.yelingziblog.article.dto;

import com.yeling.yelingziblog.article.vo.request.ArticleSearchReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSearchDTO {

    private String title;
    private List<LocalDateTime> date;
    private Integer state;
    private Integer userId;
    private String nickname;
    private String category;
    private String tag;
    private Integer isTop;
    private Integer isOriginal;
    private Integer page;
    private Integer pageSize;

    public ArticleSearchDTO(ArticleSearchReq req, Integer userId) {
        this.title = req.getTitle();
        this.date = req.getDate();
        this.state = req.getState();
        this.userId = userId;
        this.nickname = req.getNickname();
        this.category = req.getCategory();
        this.tag = req.getTag();
        this.isTop = req.getIsTop();
        this.isOriginal = req.getIsOriginal();
        this.page = req.getPage();
    }

}
