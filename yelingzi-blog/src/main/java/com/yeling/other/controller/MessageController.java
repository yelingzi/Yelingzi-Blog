package com.yeling.other.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.entity.UserContext;
import com.yeling.other.vo.request.MessageReq;
import com.yeling.other.vo.response.MessageResp;
import com.yeling.other.domian.entity.Message;
import com.yeling.other.service.MessageService;
import com.yeling.utils.IpUtils;
import com.yeling.utils.JwtUtils;
import com.yeling.entity.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 新增留言
     *
     * @param messageReq  留言数据
     * @param jwtToken JWT令牌
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/message/add")
    public ApiResponse addMessage(@Valid @RequestBody MessageReq messageReq,
                                  @RequestHeader(value = "Authorization", required = false) String jwtToken,
                                  HttpServletRequest request) {
        User user = new User();
        if (jwtToken == null || jwtToken.isEmpty()) {
            user.setId(0);
            user.setNickname("游客");
            log.info("新增留言, 游客用户");
        } else {
            user = JwtUtils.getUser(jwtToken);
            log.info("新增留言, 用户ID：{}, 名:{}", user.getId(), user.getNickname());
        }

        messageReq.setIp(IpUtils.getIpAddr(request));
        messageReq.setIpLocation("未知");
        messageService.addMessage(messageReq, user);

        return ApiResponse.success();
    }

    /**
     * 删除留言
     *
     * @param params  留言数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/interact/message/del")
    public ApiResponse delMessage(@RequestBody Map<String, Object> params) {


        User user = UserContext.getUser();

        Long id = Long.valueOf(params.get("id").toString());

        log.info("删除留言：{}, 管理员ID：{},名:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        messageService.delMessage(id);
        return ApiResponse.success();
    }

    /**
     * 通过留言
     *
     * @param params  留言数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/interact/message/pass")
    public ApiResponse updateMessage(@RequestBody Map<String, Object> params) {

        User user = UserContext.getUser();

        Long id = Long.valueOf(params.get("id").toString());

        log.info("通过留言：{}, 管理员ID：{},名:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        messageService.updateMessage(id);
        return ApiResponse.success();
    }

    /**
     * 获取一页留言列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/interact/message/page")
    public ApiResponse getMessageListByPage(@RequestParam int page,
                                            @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取留言列表，页数{},管理员Id：{},邮箱:{}", page, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<Message> pageResult = messageService.getMessageListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取留言列表失败");
        }
        return ApiResponse.success(pageResult);
    }


    /**
     * 获取留言列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/message/list")
    public ApiResponse getMessageList(){
        List<MessageResp> messageResp = messageService.getMessageList();
        if(messageResp.isEmpty()){
            return ApiResponse.error("获取留言列表失败");
        }
        return ApiResponse.success(messageResp);
    }


}
