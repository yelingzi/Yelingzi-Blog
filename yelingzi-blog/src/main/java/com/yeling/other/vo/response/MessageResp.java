package com.yeling.other.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResp {

    private String content;

    private String nickname;

    private String userAvatar;

    private Integer userId;

    private LocalDateTime createTime;

}
