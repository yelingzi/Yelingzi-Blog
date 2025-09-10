package com.yeling.talk.service;

import com.yeling.common.domian.entity.PageResult;
import com.yeling.talk.domian.entity.TalkComment;
import com.yeling.user.domian.entity.User;
import com.yeling.talk.vo.request.TalkCommentReq;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;

import java.util.List;

public interface TalkCommentService {

    List<CommentResp> getTalkCommentList(Integer talkId);

    List<CommentLikeResp> getTalkCommentLikeList(Integer talkId, Integer userId);

    CommentLikeResp addTalkCommentLike(Integer commentId, Integer talkId, Integer userId);

    void delTalkCommentLike(Integer id);

    CommentResp addTalkComment(TalkCommentReq talkCommentReq, User user);

    PageResult<TalkComment> getTalkCommentListByPage(Integer page, Integer pageSize);

    void delTalkComment(Integer id);

    void updateTalkComment(Integer id);

}
