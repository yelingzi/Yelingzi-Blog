package com.yeling.yelingziblog.article.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.article.vo.response.ArticleResp;
import com.yeling.yelingziblog.article.vo.response.CategoryResp;
import com.yeling.yelingziblog.article.vo.response.TagResp;

import java.util.ArrayList;
import java.util.List;

public class Convert {

    public static List<ArticleResp> convertToArticleRespList(List<Article> articles) {
        List<ArticleResp> articleRespList = new ArrayList<>();

        for (Article article : articles) {

            articleRespList.add(ArticleToArticleResp(article));
        }

        return articleRespList;
    }

    private static ArticleResp ArticleToArticleResp(Article article){
        ObjectMapper objectMapper = new ObjectMapper();

        ArticleResp articleResp = new ArticleResp();
        // 基本字段转换
        articleResp.setId(article.getId());
        articleResp.setNickname(article.getNickname());
        articleResp.setTitle(article.getTitle());
        articleResp.setContent(article.getBrief());
        articleResp.setArticleCover(article.getArticleCover());
        articleResp.setState(article.getState());
        articleResp.setUserId(article.getUserId());
        articleResp.setUserAvatar(article.getUserAvatar());
        articleResp.setIsOriginal(article.getIsOriginal());
        articleResp.setOriginalUrl(article.getOriginalUrl());
        articleResp.setIsTop(article.getIsTop());
        articleResp.setStarCount(article.getStarCount());
        articleResp.setCreateTime(article.getCreateTime());
        articleResp.setUpdateTime(article.getUpdateTime());
        articleResp.setReadCount(article.getReadCount());
        articleResp.setCommentCount(article.getCommentCount());
        articleResp.setLikeCount(article.getLikeCount());

        // JSON字段转换
        try {
            // 分类字段
            if (article.getCategory() != null && !article.getCategory().isEmpty()) {
                CategoryResp category = objectMapper.readValue(article.getCategory(), CategoryResp.class);
                articleResp.setCategory(category);
            }

            // 标签字段
            if (article.getTagList() != null && !article.getTagList().isEmpty()) {
                List<TagResp> tags = objectMapper.readValue(article.getTagList(), new TypeReference<List<TagResp>>() {});
                articleResp.setTagList(tags);
            }
        } catch (JsonProcessingException e) {
            // JSON解析失败时的处理逻辑
            e.printStackTrace();
        }
        return articleResp;
    }

}
