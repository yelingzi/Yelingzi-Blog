package com.yeling.yelingziblog.chat.dto;

import com.yeling.yelingziblog.chat.vo.response.ChatMessageResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushMessageData {
    private String receiver;
    private String messageType;
    private ChatMessageResp message;

}
