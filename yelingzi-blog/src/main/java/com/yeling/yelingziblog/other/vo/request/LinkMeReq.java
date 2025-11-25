package com.yeling.yelingziblog.other.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkMeReq {

    @NotBlank(message = "留言内容不能为空")
    private String content;
    @NotBlank(message = "邮箱不能为空")
    private String email;

    private String[] images;



}
