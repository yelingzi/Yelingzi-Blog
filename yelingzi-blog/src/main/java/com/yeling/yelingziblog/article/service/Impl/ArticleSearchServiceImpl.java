package com.yeling.yelingziblog.article.service.Impl;

import com.yeling.yelingziblog.article.dto.ArticleSearchDTO;
import com.yeling.yelingziblog.article.utils.Convert;
import com.yeling.yelingziblog.article.vo.request.ArticleSearchReq;
import com.yeling.yelingziblog.article.vo.response.ArticleResp;
import com.yeling.yelingziblog.common.dto.SimpleDataSearchDTO;
import com.yeling.yelingziblog.article.vo.request.ArticleSimpleSearchReq;
import com.yeling.yelingziblog.common.vo.response.SingleDataSearchResp;
import com.yeling.yelingziblog.common.dto.PageResult;
import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.article.mapper.ArticleSearchMapper;
import com.yeling.yelingziblog.article.service.ArticleSearchService;
import com.yeling.yelingziblog.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private ArticleSearchMapper articleSearchMapper;

    @Autowired
    private UserService userService;


    @Override
    public PageResult<ArticleResp> searchArticleListBySimpleDynamicCond(ArticleSimpleSearchReq req){
        List<Article> articleList = articleSearchMapper.listBySimpleDynamicCond(req);
        return new PageResult<>(articleList.size(), req.getPage(), req.getPageSize(), Convert.convertToArticleRespList(articleList));
    }

    @Override
    public PageResult<ArticleResp> searchArticleListByDynamicCond(ArticleSearchReq req){

        Integer userId = userService.getUserIdByNickname(req.getNickname());

        ArticleSearchDTO articleSearchDTO = new ArticleSearchDTO(req, userId);
        List<Article> articleList = articleSearchMapper.listByDynamicCond(articleSearchDTO);


        return new PageResult<>(articleList.size(), req.getPage(), req.getPageSize(), Convert.convertToArticleRespList(articleList));
    }

    @Override
    public List<SingleDataSearchResp> getArticleSingleDataListBySearch(String search, String type){

        List<SimpleDataSearchDTO> articleTitleSearchDTOS = switch (type) {
            case "title" -> articleSearchMapper.getArticleTitleListBySearch(search);
            case "tag" -> articleSearchMapper.getArticleTagListBySearch(search);
            case "category" -> articleSearchMapper.getArticleCategoryListBySearch(search);
            default -> new ArrayList<>();
        };
        return  articleTitleSearchDTOS.stream().map(this::convertArticleTitleSearchDTO2ArticleTitleSearchResp).toList();

    }


    private SingleDataSearchResp convertArticleTitleSearchDTO2ArticleTitleSearchResp(SimpleDataSearchDTO articleTitleSearchDTO){
        SingleDataSearchResp articleTitleSearchResp = new SingleDataSearchResp();
        articleTitleSearchResp.setId(articleTitleSearchDTO.getId());
        articleTitleSearchResp.setValue(articleTitleSearchDTO.getName());
        return articleTitleSearchResp;
    }

}
