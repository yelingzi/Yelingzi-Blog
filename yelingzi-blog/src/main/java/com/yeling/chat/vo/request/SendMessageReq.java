package com.yeling.chat.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageReq {

    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(single|group)$", message = "type 只能是 single 或 group")
    private String type;

    @NotBlank(message = "消息内容不能为空")
    private String message;

}
