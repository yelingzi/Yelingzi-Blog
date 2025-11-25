package com.yeling.yelingziblog.chatai.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppAiChatReq {

    private String prompt;
    private String sessionId;
}
