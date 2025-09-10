package com.yeling.common.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResp {

    private Integer id;

    private Integer userId;

    private String userNickname;

    private String userAvatar;

    private Integer parentId;

    private Integer toId;

    private String toNickname;

    private String content;

    private LocalDateTime createTime;

    private Integer likeCount;

    private String replyCount;

    private List<CommentResp> children;
}
