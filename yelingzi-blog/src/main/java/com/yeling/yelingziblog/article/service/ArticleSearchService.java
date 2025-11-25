package com.yeling.yelingziblog.article.service;

import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.article.vo.request.ArticleSearchReq;
import com.yeling.yelingziblog.article.vo.request.ArticleSimpleSearchReq;
import com.yeling.yelingziblog.article.vo.response.ArticleResp;
import com.yeling.yelingziblog.common.vo.response.SingleDataSearchResp;
import com.yeling.yelingziblog.common.dto.PageResult;

import java.util.List;

public interface ArticleSearchService {

    PageResult<ArticleResp> searchArticleListBySimpleDynamicCond(ArticleSimpleSearchReq req);

    PageResult<ArticleResp> searchArticleListByDynamicCond(ArticleSearchReq req);


    List<SingleDataSearchResp> getArticleSingleDataListBySearch(String search, String type);

}
