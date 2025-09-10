package com.yeling.other.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.entity.UserContext;
import com.yeling.other.vo.response.BackgroundResp;
import com.yeling.other.domian.entity.Background;
import com.yeling.other.service.BackgroundService;
import com.yeling.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class BackgroundController {

    @Autowired
    private BackgroundService backgroundService;


    /**
     * 保存背景图片
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/background/upload")
    public ApiResponse addBackgroundImageUpload(@RequestParam("image") MultipartFile multipartFile, @RequestHeader("Authorization") String jwtToken){
        User user = UserContext.getUser();

        log.info("添加背景图片，管理员ID：{},邮箱:{}", user.getId(), user.getEmail());

        backgroundService.uploadBgImage(multipartFile, user);

        return ApiResponse.success();
    }

    /**
     * 获取一页展示背景
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/background/page")
    public ApiResponse getBackgroundListByPage(@RequestParam int page,
                                               @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取一页展示背景，页数{},大小：{}，管理员Id：{},邮箱:{}", page, pageSize, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<Background> pageResult = backgroundService.getBackgroundListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取一页展示背景失败");
        }
        return ApiResponse.success(pageResult);
    }

    /**
     * 获取一页未展示背景
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/background/del/page")
    public ApiResponse getBackgroundDelListByPage(@RequestParam int page,
                                                  @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取一页未展示背景，页数{},大小：{}，管理员Id：{},邮箱:{}", page, pageSize, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<Background> pageResult = backgroundService.getBackgroundDelListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取一页展示背景失败");
        }
        return ApiResponse.success(pageResult);
    }

    /**
     * 删除背景图片
     *
     * @param params  背景图片数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/background/del")
    public ApiResponse delBg(@RequestBody Map<String, Object> params) {


        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("删除说说评论：{}, 管理员ID：{},邮箱:{}", id, Objects.requireNonNull(user).getId(), user.getEmail());

        backgroundService.delTalkComment(id);
        return ApiResponse.success();
    }

    /**
     * 展示背景图片
     *
     * @param params  背景图片数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/background/show")
    public ApiResponse showBg(@RequestBody Map<String, Object> params) {

        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("通过说说评论：{}, 管理员ID：{},邮箱:{}", id, user.getId(), user.getEmail());

        backgroundService.showTalkComment(id);
        return ApiResponse.success();
    }

    /**
     * 获取背景图片列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/background/list")
    public ApiResponse getBackgroundDelListByPage(){


        log.info("获取展示背景");

        List<BackgroundResp> list = backgroundService.getBackgroundList();
        if(list == null){
            return ApiResponse.error("获取展示背景失败");
        }
        return ApiResponse.success(list);
    }

}
