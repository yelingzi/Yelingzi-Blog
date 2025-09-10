package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMenu {
    private Integer id;
    private String classify;
    private String dishName;
    private String intro;
    private String price;
    private String dishCover;
    private String style;
    private Integer count;
}
