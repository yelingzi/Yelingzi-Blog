package com.yeling.chat.vo.response;

import com.yeling.chat.domain.dto.ChatInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewSingleChatBriefResp {

    private ChatInfoDto info;

    private List<ChatMessageResp> messages;

}
