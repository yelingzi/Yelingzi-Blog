package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassesTable {
    private Integer id;
    private String name;
    private String firWeek;
    private Integer firStart;
    private Integer firEnd;
    private String secWeek;
    private Integer secStart;
    private Integer secEnd;
    private Integer weekNumStart;
    private Integer weekNumEnd;
    private String semester;
    private Integer week;

}

