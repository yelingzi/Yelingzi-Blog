package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleComment {
    private Integer id;
    private Integer userId;
    private String userNickname;
    private String userAvatar;
    private Integer parentId;
    private Integer toId;
    private String toNickname;
    private String content;
    private LocalDateTime createTime; //创建时间
    private Integer likeCount;
    private String replyCount;
    private Integer articleId;
    private Integer state;

}
