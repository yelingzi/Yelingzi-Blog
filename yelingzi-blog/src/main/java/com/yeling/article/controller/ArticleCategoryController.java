package com.yeling.article.controller;

import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.Category;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.article.vo.request.CategoryReq;
import com.yeling.article.vo.response.CategoryResp;
import com.yeling.article.service.ArticleCategoryService;
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
public class ArticleCategoryController {


    @Autowired
    private ArticleCategoryService categoryService;

    /**
     * 新增文章分类
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping(value = "/api/admin/article/category/add")
    public ApiResponse add(@Valid @RequestBody CategoryReq categoryReq) {

        User user = UserContext.getUser();

        log.info("新增文章分类{}, 管理员：{},名{}", categoryReq.getCategoryName(), user.getId(), user.getNickname());


        CategoryResp categoryResp = categoryService.addCategory(categoryReq.getCategoryName(), user);
        if(categoryResp == null){
            return ApiResponse.error("添加文章分类错误");
        }

        return ApiResponse.success(categoryResp);
    }

    /**
     * 删除文章分类
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping(value = "/api/admin/article/category/del/{id}")
    public ApiResponse deleteArticle(@PathVariable Integer id){

        User user = UserContext.getUser();

        log.info("删除文章分类Id:{}, 管理员：{},名{}", id, user.getId(), user.getNickname());

        categoryService.deleteCategory(id);
        return ApiResponse.success();
    }

    /**
     * 获取一页文章分类列表
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/article/category/page")
    public ApiResponse getArticleCategoryListByPage(@RequestParam int page, @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取文章分类列表，页数{},管理员Id：{},邮箱:{}", page, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<Category> pageResult = categoryService.getArticleCategoryListByPage(page, pageSize);
        if(pageResult == null){
            return ApiResponse.error("获取文章分类列表失败");
        }
        return ApiResponse.success(pageResult);
    }

    /**
     * 获取一页文章分类列表
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/user/category/page")
    public ApiResponse userGetArticleCategoryListByPage(@RequestParam int page, @RequestParam int pageSize){

        User user = UserContext.getUser();

        log.info("获取文章分类列表，页数{},管理员Id：{},邮箱:{}", page, Objects.requireNonNull(user).getId(),user.getEmail());

        PageResult<Category> pageResult = categoryService.userGetArticleCategoryListByPage(page, pageSize, user.getId());
        if(pageResult == null){
            return ApiResponse.error("获取文章分类列表失败");
        }
        return ApiResponse.success(pageResult);
    }

    /**
     * 获取文章分类列表
     */
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping(value = "/api/admin/article/category/list")
    public ApiResponse getArticleCategoryList(){

        User user = UserContext.getUser();

        log.info("获取文章分类列表,管理员Id：{},邮箱:{}", user.getId(), user.getEmail());
        List<Category> categories = categoryService.getArticleCategoryList();
        if(categories.isEmpty()){
            return ApiResponse.error("获取文章分类列表失败");
        }
        return ApiResponse.success(categories);
    }

    /**
     * 获取文章分类列表
     */
    @GetMapping(value = "/api/user/article/category/list")
    public ApiResponse userGetArticleCategoryList(){

        User user = UserContext.getUser();

        log.info("获取文章分类列表,用户Id：{},邮箱:{}", user.getId(), user.getEmail());
        List<Category> categories = categoryService.userGetArticleCategoryList(user.getId());
        if(categories.isEmpty()){
            return ApiResponse.error("获取文章分类列表失败");
        }
        return ApiResponse.success(categories);
    }

    /**
     * 更新文章分类
     */
    @PostMapping(value = "/api/admin/article/category/update")
    public ApiResponse updateCategory(@Valid @RequestBody CategoryReq categoryReq) {

        User user = UserContext.getUser();

        log.info("更新文章分类Id:{},新的分类名：{}， 管理员Id：{},邮箱:{}", categoryReq.getId(), categoryReq.getCategoryName(), user.getId(),user.getEmail());

        CategoryResp categoryResp = categoryService.updateCategory(categoryReq, user);
        if(categoryResp == null){
            return ApiResponse.error("更新文章分类错误");
        }

        return ApiResponse.success(categoryResp);
    }

}
