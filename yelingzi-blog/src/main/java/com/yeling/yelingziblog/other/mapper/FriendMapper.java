package com.yeling.yelingziblog.other.mapper;

import com.yeling.yelingziblog.other.entity.Friend;
import com.yeling.yelingziblog.other.vo.response.FriendResp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FriendMapper {

    @Insert("""
            insert into friend(title, cover, introduction, url, user_id, nickname)
            values (#{title}, #{cover}, #{introduction},#{url}, #{userId}, #{nickname})
            """)
    void addFriend(String title, String cover, String introduction, String url, Integer userId, String nickname);

    @Update("""
            UPDATE friend SET state=#{state} WHERE id=#{id}
            """)
    void updateFriendStateById(Integer state, Long id);

    @Select("""
            SELECT COUNT(*) FROM friend
            """)
    int findFriendCount();

    @Select("""
            SELECT * FROM friend ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Friend> findFriendListByPage(int page, int pageSize);

    @Select("""
            SELECT title, cover, introduction, url, recommend_status,create_time FROM friend WHERE state=2 ORDER BY create_time 
            """)
    List<FriendResp> findFriendList();

}
