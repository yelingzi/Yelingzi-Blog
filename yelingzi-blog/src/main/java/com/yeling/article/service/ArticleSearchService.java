package com.yeling.article.service;

import com.yeling.article.domian.entity.ArticleInfo;

import java.util.List;

public interface ArticleSearchService {

    List<ArticleInfo> searchArticle(String search, Integer page, String classify);

    List<ArticleInfo> userSearchArticle(String search, Integer page, String classify, String jwt);

    Integer getSearchArticleCount(String search, String classify);

    List<String> getArticleClassifyBySearch(String search);
}
