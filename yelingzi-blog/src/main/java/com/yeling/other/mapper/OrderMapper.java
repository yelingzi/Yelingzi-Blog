package com.yeling.other.mapper;

import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.OrderDesk;
import com.yeling.other.domian.entity.OrderMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Select("select *from orders")
    List<OrderMenu> getMenu();

    @Select("select price from orders where dish_name =#{dishName}")
    int getMoney(OrderMenu orderMenu);



    @Insert("insert into incomes(order_num,content,money,intro,state,order_time,user_name) " +
            "values (#{orderNum},#{content},#{money},#{intro},#{state},#{orderTime},#{userName})")
    void addOrder(OrderLedger orderLedger);

    @Update("update incomes set state = #{state} " +
            "where order_num = #{orderNum}")
    void updateOrderState(String state,String orderNum);

    @Select("select * from order_desk")
    List<OrderDesk> getOrderDesk();

    @Update("update order_desk set state = #{state},order_num = #{orderNum} " +
            "where desk_name = #{orderDeskName}")
    void updateOrderDeskStateUse(OrderLedger orderLedger);

    @Update("update order_desk set state = #{state},order_num = #{orderNum} " +
            "where desk_name = #{orderDeskName}")
    void updateOrderDeskState(OrderLedger orderLedger);

    @Update("update order_desk set state = '空',order_num = '' " +
            "where desk_name = #{deskName}")
    void resetDesk(OrderDesk orderDesk);

    @Select("select * from order_desk where desk_name = #{deskName}")
    OrderDesk getDesk(OrderDesk orderDesk);

    @Insert("insert into orders(dish_name,intro,dish_cover,style,price,classify) values " +
            "(#{dishName},#{intro},#{dishCover},#{style},#{price},#{classify})")
    void addOrderMenu(OrderMenu orderMenu);

    @Delete("delete from orders where id = #{id}")
    void delOrderMenuById(Integer id);

    @Insert("insert into order_desk(desk_name,state) values " +
            "(#{deskName},#{state})")
    void addOrderDesk(OrderDesk orderDesk);

    @Delete("delete from order_desk where id = #{id}")
    void delOrderDeskById(Integer id);

}
