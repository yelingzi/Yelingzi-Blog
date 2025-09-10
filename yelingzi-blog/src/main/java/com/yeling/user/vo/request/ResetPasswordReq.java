package com.yeling.user.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReq {


    /**
     * 用户名
     */
    @Email(message = "邮箱格式不正确")
    private String email;
    /**
     * 密码
     */
    @Pattern(regexp = "^[a-fA-F0-9]{32}$", message = "密码格式不正确")
    private String password;
    /**
     * 重置密码Key
     */
    private String passwordKey;


}
