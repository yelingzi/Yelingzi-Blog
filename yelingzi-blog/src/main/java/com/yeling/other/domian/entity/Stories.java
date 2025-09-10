package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stories {
    private String userName;
    private String content;
    private String imageUrl;
    private LocalDateTime publishTime;
    private Integer id;
    private Integer userId;
}
