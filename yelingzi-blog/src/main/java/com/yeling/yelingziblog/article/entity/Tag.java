package com.yeling.yelingziblog.article.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag implements Serializable {

    private Integer id;
    private String tagName;
    private Integer articleCount;
    private LocalDateTime createTime;
    private Integer userId;
    private String nickname;
    private Integer isDel;

}
