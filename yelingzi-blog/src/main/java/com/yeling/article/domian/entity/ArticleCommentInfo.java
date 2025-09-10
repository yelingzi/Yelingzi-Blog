package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCommentInfo {
    private Integer id;
    private Integer parentId;
    private String content;
    private LocalDateTime commentTime;
    private Integer liked;
    private Integer userId;
    private String niCheng;
    private String userAvatar;
    private String replyUser;
    private List<ArticleCommentInfo> children;

    public List<ArticleCommentInfo> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }
}
