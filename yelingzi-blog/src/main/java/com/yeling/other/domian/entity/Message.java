package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private Integer id;

    private String content;

    private String nickname;

    private String ip;

    private String ipLocation;

    private Integer state;

    private String userAvatar;

    private Integer userId;

    private LocalDateTime createTime;

}
