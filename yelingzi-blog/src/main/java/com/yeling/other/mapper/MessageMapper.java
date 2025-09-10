package com.yeling.other.mapper;

import com.yeling.other.domian.entity.Message;
import com.yeling.other.vo.response.MessageResp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("""
            insert into message(content, ip, ip_location, user_id, nickname, user_avatar)
            values (#{content}, #{ip}, #{ipLocation}, #{userId}, #{nickname},#{userAvatar})
            """)
    void addMessage(String content, String ip, String ipLocation, Integer userId, String nickname, String userAvatar);

    @Update("""
            UPDATE message SET state=#{state} WHERE id=#{id}
            """)
    void updateMessageStateById(Integer state, Long id);

    @Select("""
            SELECT COUNT(*) FROM message
            """)
    int findMessageCount();

    @Select("""
            SELECT COUNT(*) FROM message WHERE message_board_user=#{userId}
            """)
    int findMessageCountByUserId(Integer userId);

    @Select("""
            SELECT * FROM message ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Message> findMessageListByPage(int page, int pageSize);

    @Select("""
            SELECT * FROM message WHERE message_board_user=#{userId} ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Message> findMessagePageListByUserId(Integer userId, int page, int pageSize);

    @Select("""
            SELECT content,user_id,user_avatar,nickname,create_time FROM message WHERE state=2
            """)
    List<MessageResp> findMessageList();

    @Select("""
            SELECT COUNT(*) FROM message WHERE state=#{state}
            """)
    int findMessageCountByState(Integer state);

}
