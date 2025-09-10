package com.yeling.other.domian.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Page {

    private Integer id;
    private Integer userId;
    private String pageName;
    private String routePath;
    private String configJson;
    private Integer status;
    private LocalDateTime createTime;
}
