package com.yeling.yelingziblog.article.controller;

import com.alibaba.fastjson2.JSON;
import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.article.service.ArticleSearchService;
import com.yeling.yelingziblog.article.vo.request.ArticleReq;
import com.yeling.yelingziblog.article.vo.request.ArticleSearchReq;
import com.yeling.yelingziblog.article.vo.request.ArticleSimpleSearchReq;
import com.yeling.yelingziblog.article.vo.response.ArticleResp;
import com.yeling.yelingziblog.common.vo.response.SingleDataSearchResp;
import com.yeling.yelingziblog.common.dto.ApiResponse;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.common.dto.UserContext;
import com.yeling.yelingziblog.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class ArticleSearchController {

    @Autowired
    private ArticleSearchService articleSearchService;


    /**
     * 根据简单参数查询文章
     */
    @PostMapping(value = "/api/admin/article/search/simple")
    public ApiResponse searchArticleListBySimpleDynamicCond(@RequestBody ArticleSimpleSearchReq req) {
        User user = UserContext.getUser();

        log.info("用户 {} 搜索文章，条件：{}, {}, {}", user.getNickname(), req.getTitle(), req.getDate(), req.getState());

        PageResult<ArticleResp> pageResult = articleSearchService.searchArticleListBySimpleDynamicCond(req);

        return ApiResponse.success(pageResult);
    }


    /**
     * 根据参数查询文章
     */
    @PostMapping(value = "/api/admin/article/search/list")
    public ApiResponse searchArticleListByDynamicCond(@RequestBody ArticleSearchReq req) {
        User user = UserContext.getUser();

        log.info("用户 {} 搜索文章，条件：{}", user.getNickname(), req);

        PageResult<ArticleResp> pageResult = articleSearchService.searchArticleListByDynamicCond(req);

        return ApiResponse.success(pageResult);
    }

    /**
     * 模糊查询文章标题列表
     */
    @GetMapping(value = "/api/admin/article/search/title")
    public ApiResponse getArticleTitleListBySearch(@RequestParam String search) {
        User user = UserContext.getUser();

        log.info("搜索文章标题列表：{}，管理员Id：{},邮箱:{}", search, Objects.requireNonNull(user).getId(),user.getEmail());

        List<SingleDataSearchResp> pageResult = articleSearchService.getArticleSingleDataListBySearch(search, "title");

        log.info("搜索文章标题列表：{}", pageResult);

        return ApiResponse.success(pageResult);
    }

    /**
     * 模糊查询文章标签列表
     */
    @GetMapping(value = "/api/admin/article/search/tag")
    public ApiResponse getArticleTagListBySearch(@RequestParam String search) {
        User user = UserContext.getUser();

        log.info("搜索文章标签列表：{}，管理员Id：{},邮箱:{}", search, Objects.requireNonNull(user).getId(),user.getEmail());

        List<SingleDataSearchResp> pageResult = articleSearchService.getArticleSingleDataListBySearch(search, "tag");

        log.info("搜索文章标签列表：{}", pageResult);

        return ApiResponse.success(pageResult);
    }

    /**
     * 模糊查询文章分类列表
     */
    @GetMapping(value = "/api/admin/article/search/category")
    public ApiResponse getArticleCategoryListBySearch(@RequestParam String search) {
        User user = UserContext.getUser();

        log.info("搜索文章分类列表：{}，管理员Id：{},邮箱:{}", search, Objects.requireNonNull(user).getId(),user.getEmail());

        List<SingleDataSearchResp> pageResult = articleSearchService.getArticleSingleDataListBySearch(search, "category");

        log.info("搜索文章分类列表：{}", pageResult);

        return ApiResponse.success(pageResult);
    }



}
