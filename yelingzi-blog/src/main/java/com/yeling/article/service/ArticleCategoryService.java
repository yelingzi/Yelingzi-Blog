package com.yeling.article.service;

import com.yeling.article.domian.entity.Category;
import com.yeling.common.domian.entity.PageResult;
import com.yeling.user.domian.entity.User;
import com.yeling.article.vo.request.CategoryReq;
import com.yeling.article.vo.response.CategoryResp;

import java.util.List;

public interface ArticleCategoryService {

    CategoryResp addCategory(String category, User user);

    void deleteCategory(Integer id);

    PageResult<Category> getArticleCategoryListByPage(int page, int pageSize);

    PageResult<Category> userGetArticleCategoryListByPage(int page, int pageSize, Integer userId);

    List<Category> getArticleCategoryList();

    List<Category> userGetArticleCategoryList(Integer userId);


    CategoryResp updateCategory(CategoryReq categoryReq, User user);

}
