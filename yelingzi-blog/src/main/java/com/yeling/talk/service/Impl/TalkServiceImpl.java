package com.yeling.talk.service.Impl;

import com.yeling.common.domian.entity.PageResult;
import com.yeling.talk.domian.entity.Talk;
import com.yeling.user.domian.entity.User;
import com.yeling.talk.vo.request.TalkReq;
import com.yeling.talk.vo.response.SimpleTalkResp;
import com.yeling.talk.vo.response.TalkResp;
import com.yeling.talk.mapper.TalkMapper;
import com.yeling.talk.service.TalkService;
import com.yeling.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TalkServiceImpl implements TalkService {

    @Value("${file.upload.savePath}")
    private String savePath;

    @Value("${file.upload.relativePath}")
    private String relativePath;

    @Value("${file.upload.allowedTypes:image/jpg,image/jpeg,image/png}")
    private String allowedTypes;

    @Value("${file.upload.maxSize:2097152}") // 默认最大2MB
    private long maxSize;

    @Autowired
    private TalkMapper talkMapper;

    @Override
    public void addTalk(TalkReq talkReq, User user){
        talkMapper.addTalks(talkReq.getTitle(), talkReq.getContent(), talkReq.getImageUrl(), talkReq.getState(),
                talkReq.getIsTop(), user.getId(), user.getNickname(), user.getUserAvatar());
    }

    @Override
    public void deleteTalk(Integer id){
        talkMapper.updateTalksStateById(1, id);
    }

    @Override
    public void regainTalk(Integer id){
        talkMapper.updateTalksStateById(0, id);
    }

    @Override
    public PageResult<Talk> getTalkListByPage(Integer page, Integer pageSize){
        PageResult<Talk> pageResult = new PageResult<>();
        pageResult.setPageSize(pageSize);
        pageResult.setPage(page);
        pageResult.setTotal(talkMapper.findTalksCount());
        pageResult.setData(talkMapper.findTalksByPage((page - 1)*pageSize, pageSize));
        return pageResult;
    }

    @Override
    public void updateTalk(TalkReq talkReq, User user){
        talkMapper.updateTalks(talkReq.getId(), talkReq.getTitle(), talkReq.getContent(), talkReq.getImageUrl(), talkReq.getState(),
                talkReq.getIsTop(), user.getId(), user.getNickname(), user.getUserAvatar());
    }

    @Override
    public String uploadTalkImage(MultipartFile multipartFile){
        return ImageUtils.uploadImage(multipartFile, "/talk", savePath, relativePath, allowedTypes, maxSize);
    }

    @Override
    public PageResult<TalkResp> getTalkList(int page, int pageSize){

        PageResult<TalkResp> pageResult = new PageResult<>();
        int total = talkMapper.findTalksCountByState(0);

        int start = (page - 1) * pageSize;

        if(start > total){
            return null;
        }
        int actualPageSize = Math.min(pageSize, total - start);

        List<Talk> topList = new ArrayList<>(talkMapper.findTalkTop(start, actualPageSize));
        int topCount = topList.size();

        if(topCount < actualPageSize && topCount > 0){
            topList.addAll(talkMapper.findTalk(0,(actualPageSize - topCount)));
        } else if(topCount == 0){
            int topTalkCount = talkMapper.findTalkRespTopCount();
            topList.addAll(talkMapper.findTalk(start - topTalkCount,actualPageSize));
        }

        List<TalkResp> respList = topList.stream().map(talk -> {
            TalkResp resp = new TalkResp();
            resp.setId(talk.getId());
            resp.setTitle(talk.getTitle());
            resp.setContent(talk.getContent());
            // 处理imageUrl（String转List）
            resp.setImageUrl(talk.getImageUrl() != null ?
                    Arrays.asList(talk.getImageUrl().split("#")) :
                    Collections.emptyList());
            resp.setIsTop(talk.getIsTop());
            resp.setUserId(talk.getUserId());
            resp.setNickname(talk.getNickname());
            resp.setUserAvatar(talk.getUserAvatar());
            resp.setCreateTime(talk.getCreateTime());
            resp.setLikeCount(talk.getLikeCount());
            resp.setCommentCount(talk.getCommentCount());
            return resp;
        }).collect(Collectors.toList());

        pageResult.setData(respList);
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        pageResult.setTotal(total);

        return pageResult;
    }

    @Override
    public TalkResp getTalkById(int id) {
        Talk talk = talkMapper.findTalkById(id);
        if (talk == null) {
            return null;
        }

        TalkResp talkResp = new TalkResp();
        talkResp.setId(talk.getId());
        talkResp.setTitle(talk.getTitle());
        talkResp.setContent(talk.getContent());

        // 将imageUrl字符串转换为List<String>
        if (talk.getImageUrl() != null && !talk.getImageUrl().isEmpty()) {
            talkResp.setImageUrl(List.of(talk.getImageUrl().split("#")));
        } else {
            talkResp.setImageUrl(Collections.emptyList());
        }

        talkResp.setIsTop(talk.getIsTop());
        talkResp.setUserId(talk.getUserId());
        talkResp.setNickname(talk.getNickname());
        talkResp.setUserAvatar(talk.getUserAvatar());
        talkResp.setCreateTime(talk.getCreateTime());
        talkResp.setLikeCount(talk.getLikeCount());
        talkResp.setCommentCount(talk.getCommentCount());

        return talkResp;
    }

    @Override
    public void updateTalkTop(Integer id, Integer isTop){
        talkMapper.updateTalkTopById(isTop, id);
    }

    @Override
    public List<SimpleTalkResp> getTopTalkList(){
        List<SimpleTalkResp> list = talkMapper.findTopTalkList(3);
        if(list.size() < 3){
            list.addAll(talkMapper.findLikeCountTalkList(3 - list.size()));
        }
        return list;
    }

    @Override
    public Integer getTalkLike(Integer id, Integer userId){
        return talkMapper.findTalkLikeByUserId(id, userId);
    }

    @Override
    @Transactional
    public void talkLike(Integer id, Integer userId){
        int num = talkMapper.findTalkLikeByUserId(id, userId);

        if(num == 0){
            talkMapper.addTalkLike(id, userId);
            //点赞加1
            talkMapper.updateTalksLikeCountById(id, 1);
        }

    }

    @Override
    @Transactional
    public void talkUnLike(Integer id, Integer userId){
        int num = talkMapper.findTalkLikeByUserId(id, userId);

        if(num == 1){
            talkMapper.delTalkLike(id, userId);
            //点赞加1
            talkMapper.updateTalksLikeCountById(id, -1);
        }

    }

}
