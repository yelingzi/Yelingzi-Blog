package com.yeling.talk.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.entity.UserContext;
import com.yeling.talk.vo.request.TalkCommentReq;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;
import com.yeling.talk.service.TalkCommentService;
import com.yeling.talk.domian.entity.TalkComment;
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
public class TalkCommentController {

    @Autowired
    private TalkCommentService talkCommentService;


    /**
     * 获取说说评论列表
     * @param articleId  说说Id
     *
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/talk/comment/list")
    public ApiResponse getArticleList(@RequestParam Integer articleId) {

        log.info("获取说说评论列表");

        List<CommentResp> list = talkCommentService.getTalkCommentList(articleId);
        if (list == null) {
            return ApiResponse.error("获取说说评论列表失败");
        }
        return ApiResponse.success(list);
    }

    /**
     * 根据用户ID获取说说评论点赞列表
     *
     * @param talkId  说说Id
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/talk/comment/like/list")
    public ApiResponse getArticleLikeList(@RequestParam Integer talkId) {

        User user = UserContext.getUser();
        log.info("获取说说评论点赞列表，用户ID:{}", user.getId());

        List<CommentLikeResp> list = talkCommentService.getTalkCommentLikeList(talkId, user.getId());
        if (list == null) {
            return ApiResponse.error("获取说说评论点赞列表失败");
        }
        return ApiResponse.success(list);
    }

    /**
     * 发表说说评论
     *
     * @param talkCommentReq  评论属性
     *
     * @return 结果
     */
    @PostMapping("/api/user/talk/comment/add")
    public ApiResponse talkComment(@Valid @RequestBody TalkCommentReq talkCommentReq){

        User user = UserContext.getUser();

        log.info("添加说说文章评论:, 用户ID：{},昵称:{}", user.getId(), user.getNickname());

        CommentResp commentResp = talkCommentService.addTalkComment(talkCommentReq, user);
        if(commentResp == null){
            return ApiResponse.error("发布评论失败");
        }

        return ApiResponse.success(commentResp);
    }

    /**
     * 给说说评论点赞
     *
     *
     * @param commentId  评论Id
     * @param talkId  说说Id
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/talk/comment/like/add")
    public ApiResponse addArticleLikeByArticleId(@RequestParam Integer commentId,
                                                 @RequestParam Integer talkId) {

        User user = UserContext.getUser();
        log.info("给说说评论{}点赞，用户ID:{}", commentId, user.getId());

        CommentLikeResp commentLikeResp = talkCommentService.addTalkCommentLike(commentId, talkId, user.getId());

        return ApiResponse.success(commentLikeResp);
    }


    /**
     * 用户取消说说评论点赞
     *
     * @param id  点赞Id
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/talk/comment/like/cancel")
    public ApiResponse delArticleLikeByArticleId(@RequestParam Integer id) {

        User user = UserContext.getUser();
        log.info("取消说说评论点赞{}，用户ID:{}", id, user.getId());

        talkCommentService.delTalkCommentLike(id);

        return ApiResponse.success();

    }

    /**
     * 获取一页说说评论列表
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/comment/talk/page")
    public ApiResponse getArticleCategoryListByPage(@RequestParam int page,
                                                    @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取文章评论列表，页数{},大小：{}，管理员Id：{},邮箱:{}", page, pageSize, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<TalkComment> pageResult = talkCommentService.getTalkCommentListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取说说评论列表失败");
        }
        return ApiResponse.success(pageResult);
    }

    /**
     * 删除说说评论
     *
     * @param params  说说评论数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/comment/talk/del")
    public ApiResponse delMessage(@RequestBody Map<String, Object> params) {


        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("删除说说评论：{}, 管理员ID：{},邮箱:{}", id, user.getId(), user.getEmail());

        talkCommentService.delTalkComment(id);
        return ApiResponse.success();
    }

    /**
     * 通过说说评论
     *
     * @param params  说说评论数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/comment/talk/pass")
    public ApiResponse updateMessage(@RequestBody Map<String, Object> params) {

        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("通过说说评论：{}, 管理员ID：{},邮箱:{}", id, user.getId(), user.getEmail());

        talkCommentService.updateTalkComment(id);
        return ApiResponse.success();
    }

}
