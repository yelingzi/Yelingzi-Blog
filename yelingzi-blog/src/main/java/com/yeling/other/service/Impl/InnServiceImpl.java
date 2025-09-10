package com.yeling.other.service.Impl;

import com.yeling.other.domian.entity.Inn;
import com.yeling.other.domian.entity.InnLedger;
import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.mapper.InnMapper;
import com.yeling.other.mapper.OrderMapper;
import com.yeling.user.mapper.UserMapper;
import com.yeling.other.service.InnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class InnServiceImpl implements InnService {

    @Autowired
    private InnMapper innMapper;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Inn> getInn(){

        return innMapper.getInn();
    }

    @Transactional
    @Override
    public Map<String, Object> submitInn(InnLedger inn, Integer uid){

        Map<String, Object> inns = new HashMap<>();

        Inn i = innMapper.getInnRoomData(inn.getRoomName());

        if( i == null ){
            return inns;
        }

        String name = userMapper.getNiChengByUid(uid);
        if(name.isEmpty()){
            return inns;
        }

        //获取房间号
        String roomNum = innMapper.getEmptyRoom(i.getRoomName());
        if (roomNum != null && !roomNum.isEmpty()) {
            inns.put("roomNum",roomNum);
            inn.setRoomName(roomNum);

            //处理订单
            OrderLedger orderLedger = new OrderLedger();

            //获取时间
            LocalDateTime localDateTime = LocalDateTime.now();
            orderLedger.setOrderTime(localDateTime);

            //获取金额
            orderLedger.setMoney(Integer.valueOf(i.getPrice()));

            //设置内容
            orderLedger.setContent("入住房间：" + i.getRoomName() + '(' + roomNum + ')' + ",入住时间："
                    + inn.getStartTime() + '-' + inn.getEndTime());

            //点单用户
            orderLedger.setUserName(name);

            //设置订单号
            String orderNum = "RY" + orderService.generateOrderNumber() + "F";
            orderLedger.setOrderNum(orderNum);
            inn.setOrderNum(orderNum);
            inns.put("orderNum",orderNum);

            //状态
            orderLedger.setState("服务中");
            inn.setState("服务中");

            //写入incomes
            orderMapper.addOrder(orderLedger);

            //写入room数据库
            innMapper.checkInRoom(inn);

            //inn房间数量减1
            innMapper.subtractInn(i.getRoomName());

        }
        return inns;

    }

    @Override
    public List<InnLedger> getInnRoom(){
        return innMapper.getInnRoom();
    }

    @Transactional
    @Override
    public Boolean addInnRoom(InnLedger innLedger){
        // 先获取待添加的 room
        String innRoom = innLedger.getRoom();

        // 判断 room 是否存在
        if (innMapper.getInnRoomCount(innRoom) > 0) {
            // 如果 room 已存在，可以选择抛出异常或者进行其他处理
            return false;
        } else {
            // 如果 room 不存在，则执行添加操作
            innLedger.setState("空");
            innMapper.addInnRoom(innLedger);
            innMapper.addInnCount(innLedger.getRoomName());
            return true;
        }
    }

    @Transactional
    @Override
    public void delInnRoom(Integer id){
        String name = innMapper.getInnRoomNameById(id);
        innMapper.delInnRoomById(id);
        innMapper.subtractInn(name);
    }

    @Transactional
    @Override
    public void innCountCorrect() {
        List<Map<String, Long>> map = innMapper.innRoomNameCount();
        updateInnCounts(map);
    }

    public void updateInnCounts(List<Map<String, Long>> map) {
        for (Map<String, Long> entry : map) {
            String name = String.valueOf(entry.get("room_name"));
            Long count = entry.get("count");
            innMapper.innCountCorrect(name, count);
        }
    }

    @Transactional
    @Override
    public Boolean addInnRoomClassify(Inn inn){
        // 先获取待添加的 room
        String innRoom = inn.getRoomName();

        // 判断 room 是否存在
        if (innMapper.getInnCount(innRoom) > 0) {
            // 如果 room 已存在，可以选择抛出异常或者进行其他处理
            return false;
        } else {
            inn.setCount(0);
            // 如果 room 不存在，则执行添加操作
            innMapper.addInn(inn);
            return true;
        }
    }

    @Transactional
    @Override
    public void delInn(Integer id){
        String string = innMapper.getInnNameById(id);
        innMapper.delInnById(id);
        innMapper.delInnRoomByRoomName(string);
    }





}
