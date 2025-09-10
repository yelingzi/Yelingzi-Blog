package com.yeling.user.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoReq {
    private Integer id;

    @Pattern(regexp = "/^\\S{2,16}$/", message = "昵称必须是2-16位的非空字符")
    private String nickname;

    private String userAvatar;

    private LocalDateTime updateTime; //修改时间

}