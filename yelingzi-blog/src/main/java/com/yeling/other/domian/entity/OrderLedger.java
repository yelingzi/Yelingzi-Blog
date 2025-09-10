package com.yeling.other.domian.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLedger {
    private String orderNum;
    private String content;
    private String intro;
    private Integer money;
    private LocalDateTime orderTime;
    private String state;
    private List<OrderMenu> orders;
    private String orderDeskName;
    private String userName;
}
