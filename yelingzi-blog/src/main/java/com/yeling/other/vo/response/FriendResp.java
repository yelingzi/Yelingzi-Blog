package com.yeling.other.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendResp {

    private String title;

    private String cover;

    private String introduction;

    private String url;

    private Integer recommendStatus;

    private LocalDateTime createTime;
}
