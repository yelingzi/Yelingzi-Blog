package com.yeling.other.mapper;

import com.yeling.other.domian.entity.Inn;
import com.yeling.other.domian.entity.InnLedger;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface InnMapper {

    @Select("select * from inn")
    List<Inn> getInn();

    @Select("select * from inn where room_name = #{name}")
    Inn getInnRoomData(String name);


    @Select("SELECT room FROM inn_room WHERE room_name = #{name} and state = '空' ORDER BY RAND() LIMIT 1;")
    String getEmptyRoom(String name);

    @Update("update inn_room set state=#{state},order_num=#{orderNum},start_time=#{startTime}," +
            "end_time=#{endTime} where room = #{roomName}")
    void checkInRoom(InnLedger innLedger);

    @Select("select * from inn_room")
    List<InnLedger> getInnRoom();

    @Update("update inn set count=count-1 where room_name = #{name}")
    void subtractInn(String name);

    @Select("select state from incomes where order_num = #{order}")
    String getInnLedger(String order);

    @Update("update incomes set state=#{state} where order_num = #{order}")
    void setInnLedger(String order,String state);

    @Insert("insert into inn_room(room_name,room,state) values " +
            "(#{roomName},#{room},#{state})")
    void addInnRoom(InnLedger innLedger);

    @Select("select COUNT(*) from inn_room where room = #{room}")
    Integer getInnRoomCount(String room);

    @Update("update inn set count=count+1 where room_name = #{name}")
    void addInnCount(String name);

    @Select("SELECT room_name FROM inn_room WHERE id=#{id}")
    String getInnRoomNameById(Integer id);

    @Delete("delete from inn_room where id = #{id}")
    void delInnRoomById(Integer id);

    @Select("SELECT room_name, COUNT(room) as count FROM inn_room GROUP BY room_name")
    List<Map<String, Long>> innRoomNameCount();

    @Update("update inn set count=#{count} where room_name = #{name}")
    void innCountCorrect(String name,Long count);

    @Insert("INSERT INTO inn(room_name,price,intro,count) " +
            "VALUES (#{roomName},#{price},#{intro},#{count})")
    void addInn(Inn inn);

    @Select("select COUNT(*) from inn_room where room = #{room}")
    Integer getInnCount(String room);

    @Delete("delete from inn where id = #{id}")
    void delInnById(Integer id);

    @Select("select room_name from inn where id = #{id}")
    String getInnNameById(Integer id);

    @Delete("delete from inn_room where room_name = #{name}")
    void delInnRoomByRoomName(String name);

}
