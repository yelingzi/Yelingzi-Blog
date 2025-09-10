package com.yeling.chat.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageReq {

    private String nickname;

    private String message;

    private String toUser;

}
