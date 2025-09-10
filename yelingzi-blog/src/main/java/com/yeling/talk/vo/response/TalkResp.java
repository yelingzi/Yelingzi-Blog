package com.yeling.talk.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkResp {

    private Integer id;

    private String title;

    private String content;

    private List<String> imageUrl;

    private Integer isTop;

    private Integer userId;

    private String nickname;

    private String userAvatar;

    private LocalDateTime createTime;

    private Integer likeCount;

    private Integer commentCount;

}
