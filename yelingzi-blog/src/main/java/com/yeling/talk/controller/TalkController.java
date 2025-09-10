package com.yeling.talk.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.entity.UserContext;
import com.yeling.talk.vo.request.TalkReq;
import com.yeling.talk.vo.response.SimpleTalkResp;
import com.yeling.talk.vo.response.TalkResp;
import com.yeling.talk.service.TalkService;
import com.yeling.talk.domian.entity.Talk;
import com.yeling.entity.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class TalkController {

    @Autowired
    private TalkService talkService;

    /**
     * 新增说说
     *
     * @param talkReq  说说数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/talk/add")
    public ApiResponse add(@Valid @RequestBody TalkReq talkReq) {
        User user = UserContext.getUser();
        log.info("新增说说, 管理员：{},名:{}",  Objects.requireNonNull(user).getId(), user.getNickname());

        talkService.addTalk(talkReq, user);

        return ApiResponse.success();
    }


    /**
     * 删除说说
     *
     * @param id       说说ID
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping(value = "/api/admin/talk/del/{id}")
    public ApiResponse deleteArticleTag(@Validated @PathVariable Integer id) {

        User user = UserContext.getUser();

        log.info("删除说说Id:{}, 管理员：{},邮箱:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        talkService.deleteTalk(id);
        return ApiResponse.success();
    }

    /**
     * 复原说说
     *
     * @param params       说说数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/talk/regain")
    public ApiResponse regainArticleTag(@RequestBody Map<String, Object> params) {

        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("复原说说Id:{}, 管理员：{},邮箱:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        talkService.regainTalk(id);
        return ApiResponse.success();
    }

    /**
     * 获取一页说说列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/talk/page")
    public ApiResponse getArticleTagListByPage(@RequestParam int page,
                                               @RequestParam int pageSize) {

        User user = UserContext.getUser();

        log.info("获取文章标签列表,页数：{}，数据大小：{},管理员ID：{},邮箱：{}", page, pageSize,user.getId(), user.getEmail());

        PageResult<Talk> talks = talkService.getTalkListByPage(page, pageSize);
        if (talks == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(talks);
    }


    /**
     * 更新说说
     *
     * @param talkReq   说说请求对象
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/talk/update")
    public ApiResponse updateCategory(@Valid @RequestBody TalkReq talkReq) {

        User user = UserContext.getUser();

        log.info("更新文章标签Id:{}, 管理员：{},名:{}", talkReq.getId(), Objects.requireNonNull(user).getId(), user.getNickname());

        talkService.updateTalk(talkReq, user);

        return ApiResponse.success();
    }


    /**
     * 保存说说图片
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/talk/upload/image")
    public ApiResponse articleImageUpload(@RequestParam("image") MultipartFile multipartFile){
        User user = UserContext.getUser();
        log.info("添加说说图片，管理员ID：{}", user.getId());

        String path = talkService.uploadTalkImage(multipartFile);
        if(path.isEmpty()){
            return ApiResponse.error("图片上传失败");
        }
        return ApiResponse.success(path);
    }

    /**
     * 获取一页说说列表
     *

     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/talk/page")
    public ApiResponse getTalkList(@RequestParam int page,
                                   @RequestParam int pageSize){

        log.info("获取说说列表");

        PageResult<TalkResp> list = talkService.getTalkList(page, pageSize);
        if(list.getData() == null){
            return ApiResponse.error("获取数据错误");
        }

        return ApiResponse.success(list);

    }

    /**
     * 根据id获取说说
     *

     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/talk/{id}")
    public ApiResponse getTalkById(@Min(value = 1, message = "说说Id必须大于 0") @PathVariable Integer id){

        log.info("获取说说,ID:{}", id);

        TalkResp talk = talkService.getTalkById(id);
        if(talk == null){
            return ApiResponse.error("获取数据错误");
        }

        return ApiResponse.success(talk);

    }

    /**
     * 设置/取消说说顶置
     *
     * @param params  文章数据
     * @param jwtToken JWT令牌
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/talk/top")
    public ApiResponse updateArticleTop(@RequestBody Map<String, Object> params,
                                        @RequestHeader("Authorization") String jwtToken) {

        Integer id = Integer.valueOf(params.get("id").toString());
        Integer isTop = Integer.valueOf(params.get("isTop").toString());

        User user = UserContext.getUser();
        if(isTop == 1){
            log.info("置顶文章:{}, 管理员ID:{}, 邮箱：{}",id, user.getId(), user.getEmail());
        }else{
            log.info("取消置顶文章:{}, 管理员ID:{}, 邮箱：{}",id, user.getId(), user.getEmail());
        }


        talkService.updateTalkTop(id, isTop);
        return ApiResponse.success();
    }


    /**
     * 获取置顶热门说说列表
     *

     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/talk/top")
    public ApiResponse getTopTalkList(){

        log.info("获取置顶说说列表");

        List<SimpleTalkResp> list = talkService.getTopTalkList();
        if(list == null){
            return ApiResponse.error("获取数据错误");
        }

        return ApiResponse.success(list);

    }

    @GetMapping ("/api/user/talk/like")
    public ApiResponse getTalkLike(@RequestParam Integer id){

        User user = UserContext.getUser();
        log.info("获取点赞状态，说说ID：{}，用户ID：{}", id, user.getId());

        Integer i = talkService.getTalkLike(id, user.getId());
        if(i == -1){
            return ApiResponse.error("获取点赞数据失败");
        }
        return ApiResponse.success(i);
    }


    @GetMapping ("/api/user/talk/like/{id}")
    public ApiResponse talkLike(@PathVariable Integer id){
        User user = UserContext.getUser();
        log.info("点赞说说ID：{}，用户ID：{}", id, user.getId());
        talkService.talkLike(id, user.getId());
        return ApiResponse.success();
    }

    @GetMapping ("/api/user/talk/unlike/{id}")
    public ApiResponse talkUnlike(@PathVariable Integer id){

        User user = UserContext.getUser();
        log.info("取消点赞说说ID：{}，用户ID：{}", id, user.getId());

        talkService.talkUnLike(id, user.getId());

        return ApiResponse.success();
    }



}
