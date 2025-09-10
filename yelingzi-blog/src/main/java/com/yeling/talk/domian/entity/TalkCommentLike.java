package com.yeling.talk.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkCommentLike {

    private Integer id;          // 自增主键
    private Integer commentId;  // 评论ID
    private Integer talkId;      // 说说ID
    private Integer userId;      // 用户ID

}
