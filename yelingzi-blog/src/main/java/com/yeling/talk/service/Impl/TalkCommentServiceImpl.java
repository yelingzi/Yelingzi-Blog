package com.yeling.talk.service.Impl;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.talk.domian.entity.TalkComment;
import com.yeling.talk.domian.entity.TalkCommentLike;
import com.yeling.user.domian.entity.User;
import com.yeling.talk.vo.request.TalkCommentReq;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;
import com.yeling.talk.mapper.TalkCommentMapper;
import com.yeling.talk.mapper.TalkMapper;
import com.yeling.talk.service.TalkCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TalkCommentServiceImpl implements TalkCommentService {

    @Autowired
    private TalkCommentMapper talkCommentMapper;

    @Autowired
    private TalkMapper talkMapper;

    @Override
    public List<CommentResp> getTalkCommentList(Integer articleId) {
        List<CommentResp> list = talkCommentMapper.findTalkCommentListByArticleId(articleId);
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
    public List<CommentLikeResp> getTalkCommentLikeList(Integer articleId, Integer userId){
        return talkCommentMapper.findTalkCommentLikeListByArticleIdAndUserId(articleId, userId);
    }

    @Override
    @Transactional
    public CommentLikeResp addTalkCommentLike(Integer commentId, Integer talkId, Integer userId){

        TalkCommentLike talkCommentLike = new TalkCommentLike();

        talkCommentLike.setCommentId(commentId);
        talkCommentLike.setUserId(userId);
        talkCommentLike.setTalkId(talkId);

        talkCommentMapper.addTalkCommentLike(talkCommentLike);

        talkCommentMapper.updateTalkCommentLikeCountById(commentId, 1);

        return new CommentLikeResp(talkCommentLike.getId(), commentId);
    }

    @Override
    @Transactional
    public void delTalkCommentLike(Integer id){

        Integer commentId = talkCommentMapper.getTalkCommentIdById(id);

        talkCommentMapper.updateTalkCommentLikeCountById(commentId, -1);

        talkCommentMapper.delTalkCommentLike(id);
    }

    @Override
    public CommentResp addTalkComment(TalkCommentReq talkCommentReq, User user){

        TalkComment talkComment = new TalkComment();
        talkComment .setContent(SensitiveWordHelper.replace(talkCommentReq.getContent()));
        talkComment .setTalkId(talkCommentReq.getTalkId());
        talkComment .setParentId(talkCommentReq.getParentId());
        talkComment .setToId(talkCommentReq.getToId());
        talkComment .setToNickname(talkCommentReq.getToNickname());
        talkComment .setUserId(user.getId());
        talkComment .setUserNickname(user.getNickname());
        talkComment .setUserAvatar(user.getUserAvatar());


        talkCommentMapper.addTalkComment(talkComment);
//        talkCommentMapper.updateTalkCommentLikeCountById(talkComment.getTalkId(), 1);

        return talkCommentMapper.findTalkCommentById(talkComment .getId());
    }

    @Override
    public PageResult<TalkComment> getTalkCommentListByPage(Integer page, Integer pageSize){
        PageResult<TalkComment> pageResult = new PageResult<>();
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(talkCommentMapper.findTalkCommentCount());
        pageResult.setData(talkCommentMapper.findTalkCommentByPage((page-1)*pageSize, pageSize));

        return pageResult;
    }

    @Override
    @Transactional
    public void delTalkComment(Integer id){
        int state = talkCommentMapper.findTalkCommentStateById(id);
        talkCommentMapper.updateTalkCommentState(id, 1);

        if(state == 2){
            talkMapper.updateTalksCommentCountById(id, -1);
        }
    }

    @Override
    @Transactional
    public void updateTalkComment(Integer id){
        talkCommentMapper.updateTalkCommentState(id, 2);
        talkMapper.updateTalksCommentCountById(id, -1);
    }

}
