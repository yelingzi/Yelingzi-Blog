package com.yeling.common.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    private int articleCount;
    private int tagCount;
    private int categoryCount;
    private int messageCount;
    private int userCount;
    private int talkCount;
    private int commentCount;
    private int viewCount;


}
