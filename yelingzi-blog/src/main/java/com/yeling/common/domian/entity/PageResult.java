package com.yeling.common.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private long total;       // 总记录数
    private int page;         // 当前页码
    private int pageSize;     // 每页大小
    private List<T> data;     // 当前页的数据列表


}
