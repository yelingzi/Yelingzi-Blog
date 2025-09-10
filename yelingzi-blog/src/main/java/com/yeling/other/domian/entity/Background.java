package com.yeling.other.domian.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Background {

    private Integer id;
    private String url;
    private Integer state;
    private Integer userId;
    private String nickname;
    private LocalDateTime createTime;
}
