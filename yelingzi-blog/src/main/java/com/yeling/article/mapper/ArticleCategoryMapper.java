package com.yeling.article.mapper;

import com.yeling.article.domian.entity.Category;
import com.yeling.article.vo.response.CategoryResp;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper {


    @Insert("""
            insert into article_category(category_name, user_id, nickname)
            values (#{category}, #{id}, #{nickname})
            """)
    void addCategory(String category, Integer id, String nickname);

    @Select("""
            SELECT id,category_name FROM article_category WHERE category_name=#{category} AND is_del=0
            """)
    CategoryResp findCategoryByCategoryName(String category);

    @Select("""
            SELECT COUNT(*) FROM article_category WHERE category_name=#{category}  AND is_del=0
            """)
    Integer findNotNullByCategoryName(String category);


    @Update("""
            UPDATE article_category SET is_del=1 WHERE id=#{id}
            """)
    void deleteCategoryById(Integer id);
    @Select("""
            SELECT * FROM article_category ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Category> findCategoryByPage(int page, int pageSize);

    @Select("""
            SELECT * FROM article_category WHERE user_id =#{userId} ORDER BY create_time DESC limit #{page},#{pageSize}
            """)
    List<Category> findCategoryByPageByUserId(Integer userId,int page, int pageSize);

    @Select("""
            SELECT * FROM article_category WHERE is_del=0 ORDER BY create_time 
            """)
    List<Category> findCategory();

    @Select("""
            SELECT * FROM article_category WHERE is_del=0 AND user_id=#{userId} ORDER BY create_time 
            """)
    List<Category> findCategoryByUserId(Integer userId);

    @Select("""
            SELECT COUNT(*) FROM article_category 
            """)
    int findCategoryCount();

    @Select("""
            SELECT COUNT(*) FROM article_category WHERE user_id =#{userId}
            """)
    int findCategoryCountByUserId(Integer userId);

    @Update("""
            UPDATE article_category SET category_name=#{category},user_id=#{userId},nickname=#{nickname} WHERE id=#{id}
            """)
    void updateCategoryById(Integer id, String category, Integer userId, String nickname);

    @Select("""
            SELECT id,category_name FROM article_category WHERE id=#{id} AND is_del=0
            """)
    CategoryResp findCategoryById(Integer id);

    @Select("""
            SELECT COUNT(*) FROM article_category WHERE id=#{id}  AND is_del=0
            """)
    Integer findNotNullById(Integer id);

    @Update("""
            UPDATE article_category SET article_count = article_count + #{count} WHERE id=#{id}
            """)
    void updateCategoryArticleCountById(Integer id, Integer count);

}
