package com.yeling.yelingziblog.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyPushMessage {

    private String status;
    private String messageType;
    private Object data;
}
