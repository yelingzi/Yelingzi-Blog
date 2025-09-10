package com.yeling.chat.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListReq {

    @Min(value = 0, message = "游标必须大于等于0")
    private int cursor;

    @NotBlank(message = "方向不能为空")
    @Pattern(regexp = "^(after|before)$", message = "direction 只能是 after 或 before")
    private String direction;

    @NotBlank(message = "类型不能为空")
    @Pattern(regexp = "^(single|group)$", message = "type 只能是 single 或 group")
    private String type;

    @Min(value = 20,  message = "limit 最小为20")
    @Max(value = 100, message = "limit 最大为100")
    private int limit;


}
