package com.yeling.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChat {

    private int id;

    private int userId;

    private String nickname;

    private String message;

    private String toGroup;

    private String ip;

    private String messageType;

    private int isDel;

    private LocalDateTime createTime;

    private String userAvatar;


}
