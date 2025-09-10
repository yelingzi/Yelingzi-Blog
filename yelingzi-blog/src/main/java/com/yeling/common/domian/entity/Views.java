package com.yeling.common.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Views {

    private Integer id;
    private LocalDateTime createTime;
    private Integer viewCount;

}
