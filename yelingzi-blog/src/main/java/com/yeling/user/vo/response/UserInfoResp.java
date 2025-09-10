package com.yeling.user.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResp {
    private Integer userId; //ID
    private String email;
    private String nickname;
    private String userAvatar;

}
