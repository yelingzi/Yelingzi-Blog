package com.yeling.chat.domain.dto;

import com.yeling.chat.vo.response.ChatMessageResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageDto {

    private String receiver;
    private String sender;
    private ChatMessageResp message;

}
