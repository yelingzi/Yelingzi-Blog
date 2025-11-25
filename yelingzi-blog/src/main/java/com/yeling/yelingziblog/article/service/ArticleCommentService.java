package com.yeling.yelingziblog.article.service;

import com.yeling.yelingziblog.article.entity.ArticleComment;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.article.vo.request.ArticleCommentReq;
import com.yeling.yelingziblog.common.vo.response.CommentLikeResp;
import com.yeling.yelingziblog.common.vo.response.CommentResp;

import java.util.List;

public interface ArticleCommentService {

    List<CommentResp> getArticleCommentList(Integer articleId);

    List<CommentLikeResp> getArticleCommentLikeList(Integer articleId, Integer userId);

    CommentLikeResp addArticleCommentLike(Integer commentId, Integer articleId, Integer userId);

    void delArticleCommentLike(Integer id);

    CommentResp addArticleComment(ArticleCommentReq articleCommentReq, User user);

    PageResult<ArticleComment> getArticleCommentListByPage(Integer page, Integer pageSize);

    PageResult<ArticleComment> getArticleCommentListByIdAndPage(Integer articleId,Integer page, Integer pageSize);

    void delArticleComment(Integer id);

    void updateArticleComment(Integer id);
}
