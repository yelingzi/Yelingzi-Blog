package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inn {

    private String roomName;
    private Integer count;
    private String price;
    private String intro;
    private Integer id;
}
