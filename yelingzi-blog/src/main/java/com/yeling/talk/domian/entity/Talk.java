package com.yeling.talk.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Talk {

    private Integer id;

    private String title;

    private String content;

    private String imageUrl;

    private Integer isTop;

    private Integer state;

    private Integer userId;

    private String nickname;

    private String userAvatar;

    private LocalDateTime createTime;

    private Integer likeCount;

    private Integer commentCount;


}
