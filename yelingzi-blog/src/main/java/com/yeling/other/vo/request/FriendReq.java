package com.yeling.other.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendReq {

    @NotBlank(message = "网页名称不能为空")
    private String title;

    @NotBlank(message = "网页封面不能为空")
    @Pattern(regexp = "^(https)://.*", message = "链接必须以 https:// 开头")
    private String cover;

    @NotBlank(message = "网页简介不能为空")
    private String introduction;

    @NotBlank(message = "网页链接不能为空")
    @Pattern(regexp = "^(https)://.*", message = "链接必须以 https:// 开头")
    private String url;



}
