package com.yeling.talk.service;

import com.yeling.common.domian.entity.PageResult;
import com.yeling.talk.domian.entity.Talk;
import com.yeling.user.domian.entity.User;
import com.yeling.talk.vo.request.TalkReq;
import com.yeling.talk.vo.response.SimpleTalkResp;
import com.yeling.talk.vo.response.TalkResp;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TalkService {

    void addTalk(TalkReq talkReq, User user);

    void deleteTalk(Integer id);

    void regainTalk(Integer id);

    PageResult<Talk> getTalkListByPage(Integer page, Integer pageSize);

    void updateTalk(TalkReq talkReq, User user);

    String uploadTalkImage(MultipartFile multipartFile);

    PageResult<TalkResp> getTalkList(int page, int pageSize);

    TalkResp getTalkById(int id);

    void updateTalkTop(Integer id, Integer isTop);

    List<SimpleTalkResp> getTopTalkList();

    Integer getTalkLike(Integer id, Integer userId);

    void talkLike(Integer id, Integer userId);

    void talkUnLike(Integer id, Integer userId);

}

