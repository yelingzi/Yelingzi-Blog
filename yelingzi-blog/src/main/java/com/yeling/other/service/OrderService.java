package com.yeling.other.service;

import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.OrderDesk;
import com.yeling.other.domian.entity.OrderMenu;

import java.util.List;

public interface OrderService {

    List<OrderMenu> getOrderMenu();

    String addOrder(OrderLedger orderLedger,Integer uid);

    List<OrderDesk> showOrderDesk();

    OrderDesk resetDesk(OrderDesk orderDesk);

    void addOrderMenu(OrderMenu orderMenu);



    void delOrderMenu(Integer id);

    void addOrderDesk(OrderDesk orderDesk);

    void delOrderDesk(Integer id);

}
