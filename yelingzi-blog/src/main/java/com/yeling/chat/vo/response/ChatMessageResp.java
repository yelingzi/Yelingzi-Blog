package com.yeling.chat.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResp {

    private String messageType;

    private String message;

    private int id;

    private String nickname;

    private int userId;

    private String userAvatar;

    private LocalDateTime createTime;

}
