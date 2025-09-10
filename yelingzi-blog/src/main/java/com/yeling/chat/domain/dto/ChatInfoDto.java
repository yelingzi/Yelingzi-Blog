package com.yeling.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfoDto {

    // 会话类型
    private String type;

    // 会话id
    private String id;

    // 会话名称
    private String nickname;

    // 会话头像
    private String avatar;

}
