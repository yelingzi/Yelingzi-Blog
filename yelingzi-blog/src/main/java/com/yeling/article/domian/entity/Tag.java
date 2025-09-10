package com.yeling.article.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    /**
     * 分类id
     */
    private Integer id;

    /**
     * 分类名
     */
    private String tagName;

    private Integer userId;
    private String nickname;
    private LocalDateTime createTime; //创建时间
    private Integer isDel;
    private Integer articleCount;

}
