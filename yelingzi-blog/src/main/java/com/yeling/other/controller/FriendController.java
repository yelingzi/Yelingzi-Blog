package com.yeling.other.controller;

import com.yeling.entity.UserContext;
import com.yeling.other.domian.entity.Friend;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.other.vo.request.FriendReq;
import com.yeling.other.vo.response.FriendResp;
import com.yeling.other.service.FriendService;
import com.yeling.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class FriendController {


    @Autowired
    private FriendService friendService;

    /**
     * 新增友链数据
     *
     * @param friendReq  友链数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/friend/add")
    public ApiResponse addMessage(@Valid @RequestBody FriendReq friendReq) {
        User user = new User();

        user.setId(0);
        user.setEmail("游客");

        log.info("新增友链, 用户ID：{}, 名:{}", Objects.requireNonNull(user).getId(), user.getNickname());

        friendService.addFriend(friendReq, user);

        return ApiResponse.success();
    }

    /**
     * 新增友链数据
     *
     * @param friendReq  友链数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/user/friend/add")
    public ApiResponse addUserMessage(@Valid @RequestBody FriendReq friendReq) {

        User user = UserContext.getUser();

        log.info("新增友链, 用户ID：{}, 名:{}", Objects.requireNonNull(user).getId(), user.getNickname());

        friendService.addFriend(friendReq, user);

        return ApiResponse.success();
    }

    /**
     * 删除友链
     *
     * @param params  友链数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/interact/friend/del")
    public ApiResponse delMessage(@RequestBody Map<String, Object> params) {

        Long id = Long.valueOf(params.get("id").toString());

        User user = UserContext.getUser();

        log.info("删除友链：{}, 管理员ID：{},名:{}", id, user.getId(), user.getNickname());

        friendService.delFriend(id);
        return ApiResponse.success();
    }

    /**
     * 通过友链
     *
     * @param params  友链数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/interact/friend/pass")
    public ApiResponse updateMessage(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());

        User user = UserContext.getUser();

        log.info("通过友链：{}, 管理员ID：{},名:{}", id, user.getId(), user.getNickname());

        friendService.updateFriend(id);
        return ApiResponse.success();
    }

    /**
     * 获取一页友链列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/interact/friend/page")
    public ApiResponse getMessageListByPage(@RequestParam int page,
                                            @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取友链列表，页数{},管理员Id：{},邮箱:{}", page, user.getId(), user.getEmail());

        PageResult<Friend> pageResult = friendService.getFriendListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取友链列表失败");
        }
        return ApiResponse.success(pageResult);
    }


    /**
     * 获取一页友链列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/friend/list")
    public ApiResponse getMessageList(){


        log.info("获取友链列表");

        List<FriendResp> pageResult = friendService.getFriendList();
        if(pageResult == null){
            return ApiResponse.error("获取友链列表失败");
        }
        return ApiResponse.success(pageResult);
    }


}
