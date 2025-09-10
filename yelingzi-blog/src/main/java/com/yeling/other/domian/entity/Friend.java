package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friend {

    private Integer id;

    private String title;

    private String cover;

    private String introduction;

    private Integer state;

    private LocalDateTime createTime;

    private String url;

    private Integer recommendStatus;

    private Integer userId;

    private String email;

}
