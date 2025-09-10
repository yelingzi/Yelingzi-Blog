package com.yeling.other.service.Impl;

import com.yeling.other.mapper.OrderMapper;
import com.yeling.user.mapper.UserMapper;
import com.yeling.other.domian.entity.OrderDesk;
import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.OrderMenu;
import com.yeling.other.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    private ScheduledExecutorService scheduler;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<OrderMenu> getOrderMenu(){
        return orderMapper.getMenu();
    }

    @Override
    public List<OrderDesk> showOrderDesk(){
        return orderMapper.getOrderDesk();
    }

    @Transactional
    @Override
    public String addOrder(OrderLedger orderLedger, Integer uid){

        //获取时间
        LocalDateTime localDateTime = LocalDateTime.now();
        orderLedger.setOrderTime(localDateTime);

        //获取金额
        List<OrderMenu> orderMenu = orderLedger.getOrders();
        orderLedger.setMoney(setMoney(orderMenu));

        //设置内容
        orderLedger.setContent(orderLedger.getOrderDeskName() + "：" + setContent(orderMenu));

        //设置订单号
        String orderNum = "RY" + generateOrderNumber() + "D";
        orderLedger.setOrderNum(orderNum);

        //状态
        orderLedger.setState("服务中");

        //点单用户
        //设置文章作者名
        String name = userMapper.getNiChengByUid(uid);
        if(name.isEmpty()){
            return null;
        }
        orderLedger.setUserName(name);

        orderMapper.addOrder(orderLedger);
        orderMapper.updateOrderDeskStateUse(orderLedger);

        //15分钟后改为已完成
        scheduleOrderCompletion(orderLedger);

        return  orderNum;
    }


    public int setMoney(List<OrderMenu> orderMenu){
        int count = 0;
        for (OrderMenu menu : orderMenu) {
            if(menu.getCount() != null && menu.getCount() != 0) {
                int money = orderMapper.getMoney(menu);
                count += money * menu.getCount();
            }

        }
         return count;
    }

    public String setContent(List<OrderMenu> orderMenu){
        StringBuilder sb = new StringBuilder();
        for (OrderMenu menu : orderMenu) {
            if(menu.getCount() != null && menu.getCount() != 0) {
                sb.append(menu.getDishName()).append("X").append(menu.getCount()).append("、");
            }

        }
        return sb.toString();
    }

    public String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
        String date = now.format(formatter);

        Random random = new Random();
        int randomNumber = random.nextInt(10000); // 生成4位随机数

        String orderNumber; // 保证随机数是4位数字
        orderNumber = date + String.format("%04d", randomNumber);

        return orderNumber;
    }



    public void scheduleOrderCompletion(OrderLedger orderLedger) {
        LocalDateTime orderTime = orderLedger.getOrderTime();
        LocalDateTime completionTime = orderTime.plusMinutes(25);

        long delay = LocalDateTime.now().until(completionTime, ChronoUnit.SECONDS);

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // 在数据库中更新订单状态为"已完成"
            orderLedger.setState("已完成");
            orderMapper.updateOrderState(orderLedger.getState(),orderLedger.getOrderNum());
            orderLedger.setOrderNum("");
            orderLedger.setState("空");
            orderMapper.updateOrderDeskState(orderLedger);

            System.out.println("订单在15分钟后标记为已完成。");
        }, delay, TimeUnit.SECONDS);
    }

    @Transactional
    @Override
    public OrderDesk resetDesk(OrderDesk orderDesk) {
        orderMapper.resetDesk(orderDesk);
        orderMapper.updateOrderState("已完成",orderDesk.getOrderNum());
        return orderMapper.getDesk(orderDesk);
    }

    @Override
    public void addOrderMenu(OrderMenu orderMenu){
        orderMapper.addOrderMenu(orderMenu);
    }


    @Override
    public void delOrderMenu(Integer id){
        orderMapper.delOrderMenuById(id);
    }

    @Override
    public void addOrderDesk(OrderDesk orderDesk){
        orderDesk.setState("空");
        orderMapper.addOrderDesk(orderDesk);
    }

    @Override
    public void delOrderDesk(Integer id){
        orderMapper.delOrderDeskById(id);
    }

}
