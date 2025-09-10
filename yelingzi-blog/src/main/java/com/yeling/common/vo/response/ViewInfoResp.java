package com.yeling.common.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewInfoResp {

    private String ip;
    private String city;
    private String nickname;
    private LocalDateTime createTime;
}
