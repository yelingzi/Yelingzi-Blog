package com.yeling.talk.mapper;


import com.yeling.talk.domian.entity.Talk;
import com.yeling.talk.vo.response.SimpleTalkResp;
import com.yeling.talk.vo.response.TalkResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TalkMapper {

    @Select("""
            SELECT COUNT(*) FROM talks 
            """)
    Integer findTalksCount();

    @Select("""
            SELECT COUNT(*) FROM talks WHERE state=#{state}
            """)
    Integer findTalksCountByState(Integer state);

    @Insert("""
            insert into talks(title,content,image_url,state,is_top,user_id,nickname,user_avatar)
            values (#{title}, #{content}, #{imageUrl}, #{state}, #{isTop}, #{userId}, #{nickname}, #{userAvatar})
            """)
    void addTalks(String title, String content, String imageUrl, Integer state,
                  Integer isTop, Integer userId, String nickname, String userAvatar);

    @Update("""
            UPDATE talks SET title=#{id},content=#{title},id=#{content},image_url=#{imageUrl},state=#{state},is_top=#{isTop},
            user_id=#{userId},nickname=#{nickname},user_avatar=#{userAvatar} WHERE id=#{id}
            """)
    void updateTalks(Integer id, String title, String content, String imageUrl,
                     Integer state, Integer isTop, Integer userId, String nickname, String userAvatar);

    @Update("""
            UPDATE talks SET state=#{state} WHERE id=#{id}
            """)
    void updateTalksStateById(Integer state, Integer id);

    @Select("""
            SELECT * FROM talks ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Talk> findTalksByPage(int page, int pageSize);


    @Select("""
            SELECT id,title,content,image_url,is_top,user_id,nickname,user_avatar,create_time,like_count,comment_count  
            FROM talks WHERE state=0 AND is_top=1 ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Talk> findTalkTop(int page, int pageSize);

    @Select("""
            SELECT id,title,content,image_url,is_top,user_id,nickname,user_avatar,create_time,like_count,comment_count 
            FROM talks WHERE state=0 AND is_top=0 ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Talk> findTalk(int page, int pageSize);

    @Select("""
            SELECT id,create_time
            FROM talks WHERE state=0 AND create_time > DATE_SUB(NOW(), INTERVAL 6 MONTH)
            """)
    List<TalkResp> findTalkListBySixMonth();

    @Select("""
            SELECT COUNT(*) FROM talks WHERE state=0 AND is_top=1
            """)
    Integer findTalkRespTopCount();

    @Select("""
            SELECT id,title,content,image_url,is_top,user_id,nickname,user_avatar,create_time,like_count,comment_count 
            FROM talks WHERE state=0 AND id=#{id}
            """)
    Talk findTalkById(int id);

    @Update("""
            UPDATE talks SET comment_count=comment_count+#{count} WHERE id=#{id}
            """)
    void updateTalksCommentCountById(Integer count, Integer id);

    @Update("""
            UPDATE talks SET is_top=#{isTop} WHERE id=#{id}
            """)
    void updateTalkTopById(Integer isTop, Integer id);

    @Select("""
            SELECT id,title,content
            FROM talks WHERE state=0 AND is_top=1 limit 0,#{size}
            """)
    List<SimpleTalkResp> findTopTalkList(Integer size);

    @Select("""
            SELECT id,title,content
            FROM talks WHERE state=0 AND is_top=0 ORDER BY create_time DESC limit 0,#{size}
            """)
    List<SimpleTalkResp> findLikeCountTalkList(Integer size);

    @Select("""
            SELECT COUNT(*) FROM talk_like WHERE talk_id=#{talkId} AND user_id=#{userId}
            """)
    Integer findTalkLikeByUserId(Integer talkId, Integer userId);

    @Insert("""
            INSERT INTO talk_like(talk_id, user_id)
            VALUES (#{talkId}, #{userId})
            """)
    void addTalkLike(Integer talkId, Integer userId);

    @Delete("""
            DELETE FROM talk_like WHERE talk_id=#{talkId} AND user_id=#{userId}
            """)
    void delTalkLike(Integer talkId, Integer userId);

    @Update("""
            UPDATE talks SET like_count=like_count+#{count} WHERE id=#{id}
            """)
    void updateTalksLikeCountById(Integer id,  Integer count);

}
