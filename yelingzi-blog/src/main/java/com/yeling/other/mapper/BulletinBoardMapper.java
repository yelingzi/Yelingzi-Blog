package com.yeling.other.mapper;

import com.yeling.other.domian.entity.UpdateLog;
import com.yeling.other.domian.entity.LeaveMessage;
import com.yeling.other.domian.entity.UpdateLogAdmin;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BulletinBoardMapper {

    @Select("select * from update_log where is_del = 0 ORDER BY is_pinned DESC, update_time DESC")
    List<UpdateLog> updateList();

    @Select("select * from update_log where is_del = 0  ORDER BY is_pinned DESC, update_time DESC")
    List<UpdateLogAdmin> AdminUpdateLogList();

    @Insert("insert into update_log(title, content) values (#{title},#{content})")
    void addLog(String title, String content);

    @Update("update update_log set title=#{title},content=#{content} where id_update_log=#{id}")
    void editLog(Integer id,String title, String content);

    @Update("update update_log set is_del=#{update} where id_update_log=#{id}")
    void delUpdateLog(Integer update,Integer id);

    @Update("update update_log set is_pinned=#{pinned} where id_update_log=#{id}")
    void pinnedLog(Integer pinned, Integer id);

    @Insert("insert into messages(content,is_read,message_time) values (#{content},#{isRead},#{time})")
    void addLeaveMessage(LeaveMessage leaveMessage);

    @Select("select * from messages ORDER BY message_time DESC LIMIT #{num},10")
    List<LeaveMessage> getLeaveMessage(Integer num);

    @Select("select * from messages WHERE is_read = #{isRead} ORDER BY message_time DESC LIMIT #{num},10")
    List<LeaveMessage> getUnreadLeaveMessage(Integer num,String isRead);

    @Select("select * from messages WHERE id = #{id}")
    LeaveMessage getIdLeaveMessage(Integer id);

    @Update("update messages set is_read=#{isRead} where id=#{id}")
    void setReadLeaveMessage(String isRead,Integer id);

    @Select("SELECT COUNT(*) FROM messages")
    Integer getLeaveMessageCount();

    @Select("SELECT COUNT(*) FROM messages WHERE is_read = '未读'")
    Integer getUnreadLeaveMessageCount();

}
