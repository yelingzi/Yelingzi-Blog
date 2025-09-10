package com.yeling.other.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.other.domian.entity.OrderDesk;
import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.OrderMenu;
import com.yeling.other.service.OrderService;
import com.yeling.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/orderMenu")
    public ApiResponse getOrderMenu(){

        log.info("获取菜单");

        List<OrderMenu> orderMenu = orderService.getOrderMenu();

        return ApiResponse.success(orderMenu);
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/user/order/order")
    public ApiResponse addOrder(@RequestBody OrderLedger orderLedger, @RequestHeader("Authorization") String jwtToken){

        User user = UserContext.getUser();

        String orderNumber = orderService.addOrder(orderLedger, user.getId());

        return ApiResponse.success(orderNumber);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping  ("/api/order/desk")
    public ApiResponse getOrderDesk(){

        log.info("查看餐桌");

        List<OrderDesk> orderDesks = orderService.showOrderDesk();

        return ApiResponse.success(orderDesks);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/desk/reset")
    public ApiResponse resetDesk(@RequestBody OrderDesk orderDesk){

        log.info("重置餐桌:{}",orderDesk.getDeskName());
        OrderDesk o = orderService.resetDesk(orderDesk);

        return ApiResponse.success(o);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/order/add")
    public ApiResponse addMenu(@RequestBody OrderMenu orderMenu){

        log.info("添加菜品:{}",orderMenu);
        orderService.addOrderMenu(orderMenu);

        return ApiResponse.success();
    }



    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping ("/api/admin/order/del/{id}")
    public ApiResponse delMenu(@PathVariable Integer id){

        log.info("删除菜品:{}",id);
        orderService.delOrderMenu(id);

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping ("/api/admin/order/desk/add")
    public ApiResponse addOrderDesk(@RequestBody OrderDesk orderDesk){

        log.info("添加餐桌:{}",orderDesk);
        orderService.addOrderDesk(orderDesk);

        return ApiResponse.success();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping ("/api/admin/order/desk/del/{id}")
    public ApiResponse delOrderDesk(@PathVariable Integer id){

        log.info("删除餐桌:{}",id);
        orderService.delOrderDesk(id);

        return ApiResponse.success();
    }


}
