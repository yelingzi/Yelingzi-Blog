package com.yeling.article.mapper;

import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.article.domian.entity.ArticleCommentLike;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleCommentMapper {

    @Select("""
    SELECT id, user_id, user_nickname, user_avatar, content, create_time, parent_id, to_id, to_nickname, reply_count, like_count
    FROM article_comment WHERE state = 0 AND article_id = #{id}
    """)
    List<CommentResp> findCommentListByArticleId(Integer id);

    @Select("""
    SELECT id,comment_id FROM article_comment_like WHERE article_id=#{articleId} AND user_id=#{userId}
    """)
    List<CommentLikeResp> findCommentLikeListByArticleIdAndUserId(Integer articleId, Integer userId);

    @Insert("""
            INSERT INTO  article_comment_like(comment_id, article_id, user_id)
            VALUES (#{commentId}, #{articleId}, #{userId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addCommentLike(ArticleCommentLike articleCommentLike);

    @Delete("""
        DELETE FROM article_comment_like WHERE id = #{id}
        """)
    void delCommentLike(Integer id);

    @Insert("""
            INSERT INTO  article_comment(content, article_id, parent_id, to_id, to_nickname, user_id, user_nickname, user_avatar)
            VALUES (#{content}, #{articleId}, #{parentId}, #{toId}, #{toNickname}, #{userId}, #{userNickname}, #{userAvatar})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void addArticleComment(ArticleComment articleComment);

    @Select("""
    SELECT id, user_id, user_nickname, user_avatar, content, create_time, parent_id, to_id, to_nickname, reply_count, like_count
    FROM article_comment WHERE id = #{id}
    """)
    CommentResp findCommentById(Integer id);

    @Update("""
            UPDATE article_comment SET like_count=like_count + #{count} WHERE id=#{id}
            """)
    void updateArticleCommentLikeCountById(Integer id, Integer count);

    @Select("""
            SELECT article_id
            FROM article_comment_like WHERE id = #{id}
    """)
    Integer getArticleCommentIdById(Integer id);

    @Select("""
            SELECT * FROM article_comment ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<ArticleComment> findArticleCommentByPage(Integer page, Integer pageSize);

    @Select("""
            SELECT COUNT(*) FROM article_comment 
            """)
    int findArticleCommentCount();

    @Update("""
            UPDATE article_comment SET state=#{state} WHERE id=#{id}
            """)
    void updateArticleCommentState(Integer id, Integer state);

    @Select("""
            SELECT state FROM article_comment WHERE id=#{id}
            """)
    int findArticleCommentStateById(Integer id);

    @Select("""
            SELECT COUNT(*) FROM article_comment WHERE state=#{state}
            """)
    int findArticleCommentCountByState(Integer state);

}
