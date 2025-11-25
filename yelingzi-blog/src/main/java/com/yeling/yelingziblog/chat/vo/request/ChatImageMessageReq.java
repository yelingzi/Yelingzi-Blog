package com.yeling.yelingziblog.chat.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatImageMessageReq {


    private String chatType;

    private MultipartFile message;

    private String toUser;
}
