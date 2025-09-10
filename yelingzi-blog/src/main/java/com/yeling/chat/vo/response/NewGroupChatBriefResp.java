package com.yeling.chat.vo.response;

import com.yeling.chat.domain.dto.ChatInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewGroupChatBriefResp {

    private ChatInfoDto info;

    private int count;

    private ChatMessageResp lastMessage;


}
