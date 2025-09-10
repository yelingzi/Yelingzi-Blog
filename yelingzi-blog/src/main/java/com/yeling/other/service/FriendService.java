package com.yeling.other.service;

import com.yeling.other.domian.entity.Friend;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.request.FriendReq;
import com.yeling.other.vo.response.FriendResp;

import java.util.List;

public interface FriendService {

    void addFriend(FriendReq friendReq, User user);

    void delFriend(Long id);

    void updateFriend(Long id);

    PageResult<Friend> getFriendListByPage(int page, int pageSize);

    List<FriendResp> getFriendList();

}
