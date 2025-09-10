package com.yeling.chat.vo.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleMessageResp {

    private String messageType;

    private String message;

    private int id;

    private String nickname;

    private int userId;

    private String createdTime;

    private String toUser;

    private boolean isFromCurrentUser;

}
