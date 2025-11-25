package com.yeling.yelingziblog.other.service.Impl;

import com.yeling.yelingziblog.other.entity.Friend;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.user.entity.User;
import com.yeling.yelingziblog.other.vo.request.FriendReq;
import com.yeling.yelingziblog.other.vo.response.FriendResp;
import com.yeling.yelingziblog.other.mapper.FriendMapper;
import com.yeling.yelingziblog.other.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;


    @Override
    public void addFriend(FriendReq friendReq, User user){
        friendMapper.addFriend(friendReq.getTitle(), friendReq.getCover(), friendReq.getIntroduction(), friendReq.getUrl(),
                user.getId(), user.getNickname());
    }

    @Override
    public void delFriend(Long id){
        friendMapper.updateFriendStateById(1, id);
    }


    @Override
    public void updateFriend(Long id){
        friendMapper.updateFriendStateById(2, id);
    }

    @Override
    public PageResult<Friend> getFriendListByPage(int page, int pageSize){
        PageResult<Friend> friendPageResult = new PageResult<>();
        friendPageResult.setPage(page);
        friendPageResult.setPageSize(pageSize);
        friendPageResult.setTotal(friendMapper.findFriendCount());
        friendPageResult.setData(friendMapper.findFriendListByPage((page -1 ) * pageSize, pageSize));
        return friendPageResult;
    }

    @Override
    @Cacheable(value = "friend:list")
    public List<FriendResp> getFriendList(){
        return friendMapper.findFriendList();
    }

}
