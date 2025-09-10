package com.yeling.chat.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListResp {

    private List<ChatMessageResp> list;

    private boolean hasMore;

}
