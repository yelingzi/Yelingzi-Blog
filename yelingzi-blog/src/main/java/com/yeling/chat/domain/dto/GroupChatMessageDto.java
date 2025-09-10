package com.yeling.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatMessageDto {

    private int id;

    private int userId;

    private String nickname;

    private String message;

    private String toGroup;

    private String ip;

    private String type;


}
