package com.yeling.article.mapper;

import com.yeling.article.domian.entity.Article;
import com.yeling.article.domian.entity.ArticleInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleSearchMapper {

    @Select("SELECT a.name, a.id, a.title, a.content, a.pictureCover, a.star, a.updateTime, a.views, a.article_comment,a.user_id, " +
            "IF(al.user_id IS NOT NULL, 1, 0) AS is_liked " +
            "FROM article a " +
            "LEFT JOIN article_like al ON a.id = al.article_id AND al.user_id = #{userId} " +
            "Where (a.title like CONCAT('%', #{search}, '%') or  a.content like CONCAT('%', #{search}, '%')) " +
            "AND a.is_del=0 ORDER BY a.updateTime DESC limit #{page},10")
    List<ArticleInfo> userSearchArticle(String search, Integer page, Integer userId);




    @Select("SELECT a.name, a.id, a.title, a.content, a.pictureCover, a.star, a.updateTime, a.views, a.article_comment,a.user_id " +
            "FROM article a " +
            "Where (a.title like CONCAT('%', #{search}, '%') or  a.content like CONCAT('%', #{search}, '%')) " +
            "AND a.is_del=0 ORDER BY a.updateTime DESC limit #{page},10")
    List<ArticleInfo> searchArticle(String search, Integer page);

    @Select("select name, id, title, content, pictureCover, star, updateTime, views from article " +
            "Where title like CONCAT('%', #{search}, '%') AND is_del=0 " +
            "ORDER BY updateTime DESC limit #{page},10")
    List<Article> searchArticleTitle(String search, Integer page);

    @Select("select name, id, title, content, pictureCover, star, updateTime, views from article " +
            "Where  content like CONCAT('%', #{search}, '%') AND is_del=0 " +
            "ORDER BY updateTime DESC limit #{page},#{end}")
    List<Article> searchArticleContent(String search, Integer page, Integer end);

    @Select("SELECT a.name, a.id, a.title, a.content, a.pictureCover, a.star, a.updateTime, a.views, a.article_comment,a.user_id, " +
            "IF(al.user_id IS NOT NULL, 1, 0) AS is_liked " +
            "FROM article a " +
            "LEFT JOIN article_like al ON a.id = al.article_id AND al.user_id = #{userId} " +
            "Where (a.title like CONCAT('%', #{search}, '%') and a.articleClassify = #{classify} " +
            " or a.content like CONCAT('%', #{search}, '%') and a.articleClassify = #{classify}) " +
            "AND a.is_del=0 ORDER BY a.updateTime DESC limit #{page},10")
    List<ArticleInfo> userSearchArticleByClassify(String search, Integer page, String classify, Integer userId);

    @Select("SELECT a.name, a.id, a.title, a.content, a.pictureCover, a.star, a.updateTime, a.views, a.article_comment, a.user_id " +
            "FROM article a " +
            "Where a.title like (CONCAT('%', #{search}, '%') and a.articleClassify = #{classify} " +
            " or a.content like CONCAT('%', #{search}, '%') and a.articleClassify = #{classify}) " +
            "AND a.is_del=0 ORDER BY a.updateTime DESC limit #{page},10")
    List<ArticleInfo> searchArticleByClassify(String search, Integer page, String classify);

    @Select("select name, id, title, content, pictureCover, star, updateTime, views from article " +
            "Where title like CONCAT('%', #{search}, '%') and articleClassify=#{classify} " +
            "AND is_del=0 ORDER BY updateTime DESC limit #{page},10")
    List<Article> searchArticleByClassifyTitle(String search, Integer page, String classify);

    @Select("select name, id, title, content, pictureCover, star, updateTime, views from article " +
            "Where content like CONCAT('%', #{search}, '%') and articleClassify = #{classify} " +
            "And is_del=0 ORDER BY updateTime DESC limit #{page},#{end}")
    List<Article> searchArticleByClassifyContent(String search, Integer page, String classify, Integer end);

    @Select("SELECT COUNT(*) FROM article " +
            "Where (title like CONCAT('%', #{search}, '%') || content like CONCAT('%', #{search}, '%'))" +
            "AND is_del=0")
    Integer getSearchArticleCount(String search);

    @Select("SELECT COUNT(*) FROM article " +
            "Where (title like CONCAT('%', #{search}, '%') or content like CONCAT('%', #{search}, '%'))" +
            "and articleClassify = #{classify} and is_del=0")
    Integer getSearchArticleClassifyCount(String search, String classify);

    @Select("SELECT DISTINCT articleClassify FROM article WHERE is_del=0 and" +
            "(title like CONCAT('%', #{search}, '%') or content like CONCAT('%', #{search}, '%'))")
    List<String> getArticleClassifyBySearch(String search);

}
