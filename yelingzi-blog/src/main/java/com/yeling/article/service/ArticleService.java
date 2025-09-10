package com.yeling.article.service;

import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.article.domian.entity.ArticleCommentInfo;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.vo.request.ArticleReq;
import com.yeling.article.vo.response.ArticleResp;
import com.yeling.article.vo.response.ArticleSortByCategoryResp;
import com.yeling.article.vo.response.TagFindArticleResp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {

    ArticleResp findArticleById(Integer id);

    ArticleResp userGetArticleById(Integer id, String jwt);

    Integer  add(ArticleReq article, User user);

    void edit(ArticleReq article, User user);

    PageResult<ArticleResp> getArticleListByPageForAdmin(Integer page, Integer pageSize);

    PageResult<ArticleResp> userGetArticleListByPage(Integer page, Integer pageSize, Integer userId);

    PageResult<ArticleResp> getArticleListByPage(Integer page, Integer pageSize);

    List<ArticleResp> getArticleList();

    List<ArticleResp> getRecommendArticleList();

    void deleteArticle(Long id);

    void regainArticle(Long id);

    void updateArticleTop(Long id, Integer isTop);

    List<ArticleSortByCategoryResp> getArticleListFromCategory();

    List<TagFindArticleResp> getArticleListByTag(Integer tagId);

    List<ArticleResp> searchArticle(String search);

    Integer getArticleLike(Integer id, Integer userId);

    Integer articleLike(Integer id, Integer userId);

    Integer articleUnlike(Integer id, Integer userId);

    String articleComment(ArticleComment articleComment, String jwt);

    List<ArticleCommentInfo> getArticleComment(Integer id);

    void addArticleComment(Integer id);

    List<ArticleComment> getAllArticleComment(Integer page);

    Integer getAllArticleCommentCount();

    List<ArticleComment> getUnPublishArticleComment(Integer page);

    Integer getUnPublishArticleCommentCount();

    String uploadArticleImage(MultipartFile multipartFile);

    String uploadArticleCover(MultipartFile multipartFile);
}
