package com.yeling.article.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.ArticleComment;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.vo.request.ArticleCommentReq;
import com.yeling.common.vo.response.CommentLikeResp;
import com.yeling.common.vo.response.CommentResp;
import com.yeling.entity.UserContext;
import com.yeling.service.ArticleCommentService;
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
public class ArticleCommentController {

    @Autowired
    private ArticleCommentService articleCommentService;


    /**
     * 获取文章评论列表
     * @param articleId  文章Id
     *
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/comment/list")
    public ApiResponse getArticleList(@RequestParam Integer articleId) {

        log.info("获取文章评论列表");

        List<CommentResp> list = articleCommentService.getArticleCommentList(articleId);
        if (list == null) {
            return ApiResponse.error("获取文章评论列表失败");
        }
        return ApiResponse.success(list);
    }

    /**
     * 根据用户ID获取文章评论点赞列表
     *
     * @param articleId  文章Id
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/article/comment/like/list")
    public ApiResponse getArticleLikeList(@RequestParam Integer articleId) {

        User user = UserContext.getUser();
        log.info("获取文章评论点赞列表，用户ID:{}", user.getId());

        List<CommentLikeResp> list = articleCommentService.getArticleCommentLikeList(articleId, user.getId());
        if (list == null) {
            return ApiResponse.error("获取文章评论点赞列表失败");
        }
        return ApiResponse.success(list);
    }


    /**
     * 发表文章评论
     *
     * @param articleCommentReq  评论属性
     *
     * @return 结果
     */
    @PostMapping("/api/user/article/comment/add")
    public ApiResponse articleComment(@Valid @RequestBody ArticleCommentReq articleCommentReq){

        User user = UserContext.getUser();

        log.info("添加文章评论:, 用户ID：{},昵称:{}", user.getId(), user.getNickname());

        CommentResp commentResp = articleCommentService.addArticleComment(articleCommentReq, user);

        return ApiResponse.success(commentResp);
    }

    /**
     * 给文章评论点赞
     *
     * @param commentId  评论Id
     * @param articleId  文章Id
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/article/comment/like/add")
    public ApiResponse addArticleLikeByArticleId(@RequestParam Integer commentId,
                                                 @RequestParam Integer articleId) {

        User user = UserContext.getUser();
        log.info("给文章评论{}点赞，用户ID:{}", commentId, user.getId());

        CommentLikeResp commentLikeResp = articleCommentService.addArticleCommentLike(commentId, articleId, user.getId());

        return ApiResponse.success(commentLikeResp);
    }


    /**
     * 用户取消文章评论点赞
     *
     * @param id  点赞Id
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/article/comment/like/cancel")
    public ApiResponse delArticleLikeByArticleId(@RequestParam Integer id) {

        User user = UserContext.getUser();
        log.info("取消文章评论点赞{}，用户ID:{}", id, user.getId());

        articleCommentService.delArticleCommentLike(id);

        return ApiResponse.success();

    }

    /**
     * 获取一页文章分类列表
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/comment/article/page")
    public ApiResponse getArticleCategoryListByPage(@RequestParam int page,
                                                    @RequestParam int pageSize) {

        User user = UserContext.getUser();

        log.info("获取文章评论列表，页数{},大小：{}，管理员Id：{},邮箱:{}", page, pageSize, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<ArticleComment> pageResult = articleCommentService.getArticleCommentListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取文章分类列表失败");
        }
        return ApiResponse.success(pageResult);
    }

    /**
     * 删除文章评论
     *
     * @param params  文章评论数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/comment/article/del")
    public ApiResponse delMessage(@RequestBody Map<String, Object> params) {


        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("删除文章评论：{}, 管理员ID：{},邮箱:{}", id, Objects.requireNonNull(user).getId(), user.getEmail());

        articleCommentService.delArticleComment(id);
        return ApiResponse.success();
    }

    /**
     * 通过文章评论
     *
     * @param params  文章评论数据
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/comment/article/pass")
    public ApiResponse updateMessage(@RequestBody Map<String, Object> params) {

        User user = UserContext.getUser();

        Integer id = Integer.valueOf(params.get("id").toString());

        log.info("通过文章评论：{}, 管理员ID：{},邮箱:{}", id,user.getId(), user.getEmail());

        articleCommentService.updateArticleComment(id);
        return ApiResponse.success();
    }

}
