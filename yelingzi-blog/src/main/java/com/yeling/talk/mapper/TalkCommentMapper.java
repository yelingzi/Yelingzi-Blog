package com.yeling.talk.mapper;

import com.yeling.talk.domian.entity.TalkComment;
import com.yeling.talk.domian.entity.TalkCommentLike;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TalkCommentMapper {

    @Select("""
            SELECT id, user_id, user_nickname, user_avatar, content, create_time, parent_id, to_id, to_nickname, reply_count, like_count
            FROM talk_comment WHERE state = 0 AND talk_id = #{talkId}
    """)
    List<CommentResp> findTalkCommentListByArticleId(Integer talkId);

    @Select("""
            SELECT id,comment_id FROM talk_comment_like WHERE talk_id=#{talkId} AND user_id=#{userId}
    """)
    List<CommentLikeResp> findTalkCommentLikeListByArticleIdAndUserId(Integer talkId, Integer userId);

    @Insert("""
            INSERT INTO  talk_comment_like(comment_id, talk_id, user_id)
            VALUES (#{commentId}, #{talkId}, #{userId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addTalkCommentLike(TalkCommentLike talkCommentLike);

    @Delete("""
            DELETE FROM talk_comment_like WHERE id = #{id}
        """)
    void delTalkCommentLike(Integer id);

    @Insert("""
            INSERT INTO  talk_comment(content, talk_id, parent_id, to_id, to_nickname, user_id, user_nickname, user_avatar)
            VALUES (#{content}, #{talkId}, #{parentId}, #{toId}, #{toNickname}, #{userId}, #{userNickname}, #{userAvatar})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addTalkComment(TalkComment talkComment);

    @Select("""
            SELECT id, user_id, user_nickname, user_avatar, content, create_time, parent_id, to_id, to_nickname, reply_count, like_count
            FROM talk_comment WHERE id = #{id}
    """)
    CommentResp findTalkCommentById(Integer id);

    @Update("""
            UPDATE talk_comment SET like_count=like_count + #{count} WHERE id=#{id}
            """)
    void updateTalkCommentLikeCountById(Integer id, Integer count);

    @Select("""
            SELECT talk_id
            FROM talk_comment_like WHERE id = #{id}
    """)
    Integer getTalkCommentIdById(Integer id);

    @Select("""
            SELECT * FROM talk_comment ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<TalkComment> findTalkCommentByPage(Integer page, Integer pageSize);

    @Select("""
            SELECT COUNT(*) FROM talk_comment 
            """)
    int findTalkCommentCount();

    @Update("""
            UPDATE talk_comment SET state=#{state} WHERE id=#{id}
            """)
    void updateTalkCommentState(Integer id, Integer state);

    @Select("""
            SELECT state FROM talk_comment WHERE id=#{id}
            """)
    int findTalkCommentStateById(Integer id);

    @Select("""
            SELECT COUNT(*) FROM talk_comment WHERE state=#{state}
            """)
    int findTalkCommentCountByState(Integer state);

}
