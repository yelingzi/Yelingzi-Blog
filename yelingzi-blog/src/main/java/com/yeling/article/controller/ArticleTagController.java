package com.yeling.article.controller;

import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.domian.entity.Tag;
import com.yeling.user.domian.entity.User;
import com.yeling.article.vo.request.TagReq;
import com.yeling.article.vo.response.TagResp;
import com.yeling.article.service.ArticleTagService;
import com.yeling.entity.UserContext;
import com.yeling.entity.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@Slf4j
@RestController
public class ArticleTagController {


    @Autowired
    private ArticleTagService articleTagService;


    /**
     * 新增文章标签
     *
     * @param tagReq  标签名称
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/tag/add")
    public ApiResponse add(@Valid @RequestBody TagReq tagReq) {

        User user = UserContext.getUser();

        log.info("新增文章标签{}, 管理员：{},名:{}", tagReq.getTagName(), Objects.requireNonNull(user).getId(), user.getNickname());

        if (tagReq.getTagName().isEmpty()) {
            return ApiResponse.error("参数错误");
        }

        TagResp tagResp = articleTagService.addTag(tagReq.getTagName(), user);
        if (tagResp == null) {
            return ApiResponse.error("添加文章标签错误");
        }

        return ApiResponse.success(tagResp);
    }

    /**
     * 删除文章标签
     *
     * @param id       标签ID
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping(value = "/api/admin/article/tag/del/{id}")
    public ApiResponse deleteArticleTag(@PathVariable Integer id) {

        User user = UserContext.getUser();

        log.info("删除标签分类Id:{}, 管理员：{},名:{}", id, Objects.requireNonNull(user).getId(), user.getNickname());

        articleTagService.deleteTag(id);
        return ApiResponse.success();
    }

    /**
     * 获取一页文章标签列表
     *
     * @param page     当前页码
     * @param pageSize 每页大小
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/article/tag/page")
    public ApiResponse getArticleTagListByPage(@RequestParam int page,
                                               @RequestParam int pageSize) {

        User user = UserContext.getUser();

        log.info("获取文章标签列表,页数：{},管理员：{},名:{}", page, Objects.requireNonNull(user).getId(), user.getNickname());

        PageResult<Tag> tags = articleTagService.getArticleTagListByPage(page, pageSize);
        if (tags == null) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(tags);
    }



    /**
     * 获取文章标签列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/article/tag/list")
    public ApiResponse getArticleTagList() {

        User user = UserContext.getUser();

        log.info("获取文章标签列表，管理员：{},名：{}", Objects.requireNonNull(user).getId(), user.getNickname());

        List<Tag> tags = articleTagService.getArticleTagList();
        if (tags.isEmpty()) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(tags);
    }

    /**
     * 更新文章标签
     *
     * @param tagReq   标签请求对象
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/tag/update")
    public ApiResponse updateCategory(@Valid @RequestBody TagReq tagReq) {

        User user = UserContext.getUser();

        log.info("更新文章标签Id:{},新的标签名：{}， 管理员：{},名:{}", tagReq.getId(), tagReq.getTagName(), Objects.requireNonNull(user).getId(), user.getNickname());

        articleTagService.updateTag(tagReq, user);

        return ApiResponse.success();
    }

    /**
     * 获取文章标签列表
     *
     * @return 结果
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/article/tag/list")
    public ApiResponse getTagList() {


        log.info("获取文章标签列表");

        List<TagResp> tags = articleTagService.getArticleTagRespList();
        if (tags.isEmpty()) {
            return ApiResponse.error("获取文章标签列表失败");
        }
        return ApiResponse.success(tags);
    }






}
