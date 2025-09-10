package com.yeling.article.service.Impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.article.domian.entity.ArticleCommentLike;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.article.vo.request.ArticleCommentReq;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;
import com.yeling.article.mapper.ArticleCommentMapper;
import com.yeling.article.mapper.ArticleMapper;
import com.yeling.service.ArticleCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleCommentServiceImpl implements ArticleCommentService {

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public List<CommentResp> getArticleCommentList(Integer articleId) {
        List<CommentResp> list = articleCommentMapper.findCommentListByArticleId(articleId);
        if (list == null) {
            return new ArrayList<>();
        }

        // 创建一个 Map，用于存储所有评论，键为评论的 id
        Map<Integer, CommentResp> commentMap = new HashMap<>();
        // 创建一个 List，用于存储顶级评论（parentId 为 0 的评论）
        List<CommentResp> rootComments = new ArrayList<>();

        for (CommentResp comment : list) {
            // 将每个评论放入 Map 中
            commentMap.put(comment.getId(), comment);
            // 初始化每个评论的 children 列表
            comment.setChildren(new ArrayList<>());
            // 如果是顶级评论，加入 rootComments 列表
            if (comment.getParentId() == 0) {
                rootComments.add(comment);
            }
        }

        // 遍历所有评论，将子评论放入对应父评论的 children 列表中
        for (CommentResp comment : list) {
            if (comment.getParentId() != 0) {
                // 获取父评论
                CommentResp parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    // 将当前评论加入父评论的 children 列表
                    parentComment.getChildren().add(comment);
                }
            }
        }

        return rootComments;
    }

    @Override
    public List<CommentLikeResp> getArticleCommentLikeList(Integer articleId, Integer userId){
        return articleCommentMapper.findCommentLikeListByArticleIdAndUserId(articleId, userId);
    }

    @Override
    @Transactional
    public CommentLikeResp addArticleCommentLike(Integer commentId, Integer articleId, Integer userId){
        ArticleCommentLike articleCommentLike = new ArticleCommentLike();

        articleCommentLike.setCommentId(commentId);
        articleCommentLike.setUserId(userId);
        articleCommentLike.setArticleId(articleId);

        articleCommentMapper.addCommentLike(articleCommentLike);
        articleCommentMapper.updateArticleCommentLikeCountById(articleCommentLike.getCommentId(), 1);
        return new CommentLikeResp(articleCommentLike.getId(), commentId);
    }

    @Override
    @Transactional
    public void delArticleCommentLike(Integer id){

        Integer commentId = articleCommentMapper.getArticleCommentIdById(id);
        articleCommentMapper.updateArticleCommentLikeCountById(commentId, -1);
        articleCommentMapper.delCommentLike(id);
    }

    @Override
    @Transactional
    public CommentResp addArticleComment(ArticleCommentReq articleCommentReq, User user){

        ArticleComment articleComment = new ArticleComment();

        articleComment.setContent(SensitiveWordHelper.replace(articleCommentReq.getContent()));
        articleComment.setArticleId(articleCommentReq.getArticleId());
        articleComment.setParentId(articleCommentReq.getParentId());
        articleComment.setToId(articleCommentReq.getToId());
        articleComment.setToNickname(articleCommentReq.getToNickname());
        articleComment.setUserId(user.getId());
        articleComment.setUserNickname(user.getNickname());
        articleComment.setUserAvatar(user.getUserAvatar());


        articleCommentMapper.addArticleComment(articleComment);
//        articleMapper.updateArticleCommentCount(articleComment.getArticleId(), 1);

        return articleCommentMapper.findCommentById(articleComment.getId());
    }

    @Override
    public PageResult<ArticleComment> getArticleCommentListByPage(Integer page, Integer pageSize){
        PageResult<ArticleComment> pageResult = new PageResult<>();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setData(articleCommentMapper.findArticleCommentByPage((page-1)*pageSize, pageSize));
        pageResult.setTotal(articleCommentMapper.findArticleCommentCount());

        return pageResult;
    }

    @Override
    @Transactional
    public void delArticleComment(Integer id){
        int state = articleCommentMapper.findArticleCommentStateById(id);
        articleCommentMapper.updateArticleCommentState(id, 1);

        if(state == 2){
            articleMapper.updateArticleCommentCount(id, -1);
        }
    }

    @Override
    @Transactional
    public void updateArticleComment(Integer id){
        articleCommentMapper.updateArticleCommentState(id, 2);
        articleMapper.updateArticleCommentCount(id, 1);
    }

}
