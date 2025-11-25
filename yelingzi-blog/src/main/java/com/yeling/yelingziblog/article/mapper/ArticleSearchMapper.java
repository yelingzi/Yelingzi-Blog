package com.yeling.yelingziblog.article.mapper;

import com.yeling.yelingziblog.article.dto.ArticleSearchDTO;
import com.yeling.yelingziblog.article.vo.request.ArticleSearchReq;
import com.yeling.yelingziblog.common.dto.SimpleDataSearchDTO;
import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.article.vo.request.ArticleSimpleSearchReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleSearchMapper {

    List<Article> listBySimpleDynamicCond(ArticleSimpleSearchReq  req);

    List<Article> listByDynamicCond(ArticleSearchDTO articleSearchDTO);

    List<SimpleDataSearchDTO> getArticleTitleListBySearch(String search);

    List<SimpleDataSearchDTO> getArticleTagListBySearch(String search);

    List<SimpleDataSearchDTO> getArticleCategoryListBySearch(String search);


}
