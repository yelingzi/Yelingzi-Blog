package com.yeling.article.mapper;

import com.yeling.article.domian.entity.Tag;
import com.yeling.article.vo.response.TagResp;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleTagMapper {


    @Insert("""
            insert into article_tag(tag_name, user_id, nickname)
            values (#{tag}, #{id}, #{nickname})
            """)
    void addTag(String tag, Integer id, String nickname);

    @Select("""
            SELECT id,tag_name FROM article_tag WHERE tag_name=#{tag} AND is_del=0
            """)
    TagResp findTagByTagName(String tag);

    @Select("""
            SELECT COUNT(*) FROM article_tag WHERE tag_name=#{tag}  AND is_del=0
            """)
    Integer findNotNullByTagName(String tag);


    @Update("""
            UPDATE article_tag SET is_del=1 WHERE id=#{id}
            """)
    void deleteTagById(Integer id);
    @Select("""
            SELECT * FROM article_tag ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Tag> findTagByPage(int page, int pageSize);

    @Select("""
            SELECT * FROM article_tag WHERE user_id=#{userId} ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Tag> findTagPageByUserId(Integer userId, int page, int pageSize);

    @Select("""
            SELECT * FROM article_tag WHERE is_del=0 ORDER BY create_time 
            """)
    List<Tag> findTag();

    @Select("""
            SELECT COUNT(*) FROM article_tag 
            """)
    int findTagCount();

    @Select("""
            SELECT COUNT(*) FROM article_tag WHERE user_id=#{userId}
            """)
    int findTagCountByUserId(Integer userId);

    @Update("""
            UPDATE article_tag SET tag_name=#{tag},user_id=#{userId},nickname=#{nickname} WHERE id=#{id}
            """)
    void updateTagById(Integer id, String tag, Integer userId, String nickname);

    @Select("""
            SELECT id,tag_name FROM article_tag WHERE id=#{id} AND is_del=0
            """)
    TagResp findTagById(Integer id);



    @Select("""
            SELECT COUNT(*) FROM article_tag WHERE id=#{id}  AND is_del=0
            """)
    Integer findNotNullById(Integer id);

    @Select("""
            SELECT id,tag_name,article_count FROM article_tag WHERE is_del=0
            """)
    List<TagResp> findTagResp();

    @Update("""
            UPDATE article_tag SET article_count = article_count + #{count} WHERE id=#{id}
            """)
    void updateTagArticleCountById(Integer id, Integer count);

}
