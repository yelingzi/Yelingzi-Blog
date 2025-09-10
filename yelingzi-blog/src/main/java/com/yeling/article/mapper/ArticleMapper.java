package com.yeling.article.mapper;

import com.yeling.article.domian.entity.Article;
import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.article.domian.entity.ArticleCommentInfo;
import com.yeling.article.vo.response.ArticleResp;
import com.yeling.article.vo.response.SimpleArticleResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {

    @Select("""
            select id,nickname,title,content,article_cover,category,tag_list,state,user_id,user_avatar,
            is_original,original_url,is_top,star_count,create_time,update_time,read_count,comment_count,like_count
            from article WHERE state=0 AND id=#{id}
            """)
    Article findArticleById(Integer id);

    @Select("SELECT a.nickname, a.id, a.title, a.content, a.category, a.star, a.update_time, a.views, a.article_comment,a.user_id, " +
            "IF(al.user_id IS NOT NULL, 1, 0) AS is_liked " +
            "FROM article a " +
            "LEFT JOIN article_like al ON a.id = al.article_id AND al.user_id = #{uid} " +
            "WHERE is_del=0 and a.id=#{id}")
    ArticleResp userGetArticleById(Integer id, Integer uid);

    @Select("SELECT article_url from article WHERE is_del=0 and id=#{id} ")
    String getArticleUrl(Integer id);

    @Insert("""
            insert into article
            (nickname,title,content,article_cover,category,article_url,tag_list,user_id,
            user_avatar,is_original,original_url,brief) 
            values (#{nickname},#{title},#{content},#{articleCover},#{category},#{articleUrl},#{tagList},
            #{userId},#{userAvatar},#{isOriginal},#{originalUrl},#{brief})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Article article);

    @Update("""
        UPDATE article
        SET nickname = #{nickname},title = #{title},content = #{content},article_cover = #{articleCover},
            category = #{category},article_url = #{articleUrl},tag_list = #{tagList},user_id = #{userId},
            user_avatar = #{userAvatar},is_original = #{isOriginal},original_url = #{originalUrl},brief=#{brief}
        WHERE id = #{id}
        """)
    void update(Article article);

    @Select("select * from article order by id desc ")
    List<Article> find();

    @Select("""
            select id,nickname,title,brief,article_cover,category,tag_list,state,user_id,user_avatar,
            is_original,original_url,is_top,star_count,create_time,update_time,read_count,comment_count,like_count
            from article WHERE state=0 order by update_time desc
            """)
    List<ArticleResp> findArticleRespList();

    @Select("""
            select id,title,brief,category,create_time
            from article WHERE state=0 order by update_time desc
            """)
    List<SimpleArticleResp> findSimpleArticleList();

    @Select("""
            select id,create_time
            from article WHERE state=0 AND create_time > DATE_SUB(NOW(), INTERVAL 6 MONTH)
            """)
    List<SimpleArticleResp> findSimpleArticleListBySixMonth();

    @Select("""
    SELECT id, nickname, title, brief, article_cover, category, tag_list, state, user_id, user_avatar,
           is_original, original_url, is_top, star_count, create_time, update_time, read_count, comment_count, like_count
    FROM article
    WHERE state = 0 AND is_top = 1
    ORDER BY update_time DESC
    LIMIT #{start}, #{pageSize}
    """)
    List<Article> findTopArticleRespListByPage(int start, int pageSize);

    @Select("select COUNT(*) from article WHERE state=0 AND is_top=1 ")
    Integer findTopArticleCount();

    @Select("""
    SELECT id, nickname, title, brief, article_cover, category, tag_list, state, user_id, user_avatar,
           is_original, original_url, is_top, star_count, create_time, update_time, read_count, comment_count, like_count
    FROM article
    WHERE state = 0 AND is_top = 0
    ORDER BY update_time DESC
    LIMIT #{start}, #{pageSize}
    """)
    List<Article> findLatestArticleRespListByPage(int start, int pageSize);

    @Select("""
                SELECT id, title, brief, article_cover, create_time
                FROM article
                WHERE state = 0 AND is_top = 0
                ORDER BY like_count DESC
                LIMIT 0, #{pageSize}
            """)
    List<Article> findArticleRespListByLikeCountDescPage(int pageSize);

    @Select("SELECT COUNT(*) FROM article WHERE state = 0")
    Integer countArticleRespList();

    @Select("""
            SELECT * FROM article ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Article> findArticleByPage(int page, int pageSize);

    @Select("""
            SELECT * FROM article WHERE user_id=#{userId} ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Article> findArticleByPageByUserId(int page, int pageSize, Integer userId);

    @Select("""
            SELECT COUNT(*) FROM article 
            """)
    Integer findArticleCount();

    @Select("""
            SELECT COUNT(*) FROM article WHERE user_id=#{userId}
            """)
    Integer findArticleCountByUserId(Integer userId);

    @Select("""
                SELECT id, nickname, title, brief, article_cover, category, tag_list, state, user_id, user_avatar,
                   is_original, original_url, is_top, star_count, create_time, update_time, read_count, comment_count, like_count
                FROM article
                WHERE state = 0 AND title like CONCAT('%', #{search}, '%')
                ORDER BY update_time 
            """)
    List<Article> findArticleRespListBySearch(String search);


    @Select("""
                SELECT id,  title, content, article_cover, category, tag_list, create_time
                FROM article WHERE state = 0 AND id=#{id}
            """)
    Article findArticleByTag(Integer id);

    @Select({
            "<script>",
            "SELECT id, title, brief, article_cover, category, tag_list, create_time",
            "FROM article",
            "WHERE state = 0 AND id IN",
            "<foreach item='item' collection='ids' open='(' separator=',' close=')'>",
            "   #{item}",
            "</foreach>",
            "</script>"
    })
    List<Article> findArticlesByIds(@Param("ids") List<Integer> ids);

    @Insert("""
            INSERT INTO  article_tag_link(tag_id, article_id)
            VALUES (#{tagId}, #{articleId})
            """)
    void addArticleTagLink(Integer tagId, Integer articleId);

    @Delete("""
            DELETE FROM article_tag_link WHERE tag_id=#{tagId} AND article_id=#{articleId}
            """)
    void delArticleTagLink(Integer tagId, Integer articleId);

    @Select("""
                SELECT article_id FROM article_tag_link
                WHERE tag_id=#{tagId}
            """)
    List<Integer> findArticleIdByTagId(Integer tagId);

    @Update("update article set state = #{update} where id = #{id}")
    void updateArticleState(Integer update, Long id);

    @Update("update article set is_top = #{isTop} where id = #{id}")
    void updateArticleTop(Long id, Integer isTop);



    @Update("update article set read_count = read_count + 1 where id = #{id}")
    void addArticleReadCount(Integer id);



    @Insert("insert into article_like(article_id,user_id) " +
            "values (#{articleId},#{userId})")
    void articleLike(Integer articleId, Integer userId);



    @Select("SELECT COUNT(*) FROM article_like WHERE article_id=#{id} AND user_id =#{uid} ")
    Integer userLikeByArticleId(Integer id, Integer uid);

    @Delete("delete from article_like where article_id = #{id} and user_id = #{uid}")
    void articleUnlike(Integer id, Integer uid);

//    @Insert("insert into article_comment(article_id,user_id,content,comment_time) " +
//            "values (#{articleId},#{userId},#{content},#{commentTime})")
//    void articleComment(ArticleComment articleComment);

    @Insert("insert into article_comment(article_id,user_id,content,update_time,parent_id, reply_user) " +
            "values (#{articleId},#{userId},#{content},#{commentTime},#{parentId},#{replyUser}) ")
    void articleComment(ArticleComment articleComment);

    /**
     * 点赞+1
     */
    @Update("update article set like_count = like_count + #{num} where id = #{articleId} ")
    void updateArticleLike(Integer articleId, Integer num);

    /**
     * 评论+num
     */
    @Update("update article set comment_count = comment_count + #{num} where id = #{articleId} ")
    void updateArticleCommentCount(Integer articleId, Integer num);

    @Select("SELECT ac.id, ac.article_id, ac.parent_id, ac.content, ac.update_time, ac.liked, ac.reply_user, ui.user_id, ui.ni_cheng, ui.user_avatar " +
            "FROM  article_comment ac  JOIN  user_info ui ON ac.user_id = ui.user_id WHERE ac.article_id = #{id} " +
            "AND ac.is_del = 0 AND ac.is_publish = 1")
    List<ArticleCommentInfo> getArticleComment(Integer id);

    @Update("update article_comment set is_publish =  #{num} where id = #{articleId} ")
    void articleCommentPublish(Integer articleId, Integer num);

    @Select("SELECT article_id FROM article_comment WHERE id = #{id}")
    Integer getArticleIdByComment(Integer id);

    @Select("SELECT * FROM article_comment ORDER BY update_time DESC limit #{page},#{num} ")
    List<ArticleComment> getAllArticleComment(Integer page,Integer num);

    @Select("SELECT COUNT(*) FROM article_comment ")
    Integer getAllArticleCommentCount();

    @Select("SELECT * FROM article_comment WHERE is_publish = 0 ORDER BY update_time DESC limit #{page},#{num} ")
    List<ArticleComment> getUnPublishArticleComment(Integer page,Integer num);

    @Select("SELECT COUNT(*) FROM article_comment WHERE is_publish = 0 ")
    Integer getUnPublishArticleCommentCount();

}
