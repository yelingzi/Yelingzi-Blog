package com.yeling.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListDto {

    private int userId;

    private String nickname;

    private String toChat;

    private int cursor;

    private int limit;

}
