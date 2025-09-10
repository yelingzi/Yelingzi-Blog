package com.yeling.service;

import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.article.vo.request.ArticleCommentReq;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;

import java.util.List;

public interface ArticleCommentService {

    List<CommentResp> getArticleCommentList(Integer articleId);

    List<CommentLikeResp> getArticleCommentLikeList(Integer articleId, Integer userId);

    CommentLikeResp addArticleCommentLike(Integer commentId, Integer articleId, Integer userId);

    void delArticleCommentLike(Integer id);

    CommentResp addArticleComment(ArticleCommentReq articleCommentReq, User user);

    PageResult<ArticleComment> getArticleCommentListByPage(Integer page, Integer pageSize);

    void delArticleComment(Integer id);

    void updateArticleComment(Integer id);
}
