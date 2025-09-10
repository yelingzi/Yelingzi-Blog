package com.yeling.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleChatMessageDto {

    private int id;

    private int userId;

    private String nickname;

    private String message;

    private String toUser;

    private String toNickname;

    private String ip;

    private String type;


}
