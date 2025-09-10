package com.yeling.other.domian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLogAdmin {
    private Integer idUpdateLog;
    private String title;//公告版本
    private String content;//优化内容
    private LocalDateTime updateTime;//更新时间
    private Integer isPinned;
    private Integer isDel;
}
