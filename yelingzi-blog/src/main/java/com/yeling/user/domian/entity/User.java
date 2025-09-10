package com.yeling.user.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id; //ID
    private String email;
    private String nickname; //
    private String password;
    private String role;//权限
    private String userAvatar;
    private LocalDateTime createTime; //创建时间
    private LocalDateTime updateTime; //修改时间
}
