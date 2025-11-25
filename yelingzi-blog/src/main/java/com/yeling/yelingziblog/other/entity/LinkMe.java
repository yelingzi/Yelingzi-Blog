package com.yeling.yelingziblog.other.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkMe {


    private Integer id;
    private String content;
    private String email;
    private String images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String status;
}
