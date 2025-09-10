package com.yeling.article.service.Impl;

import com.yeling.user.domian.entity.User;
import com.yeling.article.domian.entity.Article;
import com.yeling.article.domian.entity.ArticleInfo;
import com.yeling.utils.JwtUtils;
import com.yeling.article.mapper.ArticleSearchMapper;
import com.yeling.article.service.ArticleSearchService;
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


    /**
     *
     * @param search 搜索
     * @param page 页码
     * @param classify 分类
     * @return 文章数据
     */
    @Override
    public List<ArticleInfo> userSearchArticle(String search, Integer page, String classify, String jwt) {
        //获取数据
        Integer integer = page * 10;

        User user = JwtUtils.getUser(jwt);

        if(classify.equals("全部")){
            return articleSearchMapper.userSearchArticle(search, integer,user.getId());
        }else{
            return articleSearchMapper.userSearchArticleByClassify(search,integer,classify,user.getId());
        }

    }

    @Override
    public List<ArticleInfo> searchArticle(String search, Integer page, String classify) {
        //获取数据
        Integer integer = page * 10;


        if(classify.equals("全部")){
            return articleSearchMapper.searchArticle(search, integer);
        }else{
            return articleSearchMapper.searchArticleByClassify(search,integer,classify);
        }

    }

    /**
     *
     * @param articleInfos 文章
     * @return 文章
     */
    public List<ArticleInfo> getUserArticleStar(List<ArticleInfo> articleInfos, Integer userId){

        return articleInfos;
    }

    public List<Article> searchArticleS(String search, Integer page, String classify){
        //获取数据
        Integer integer = page * 10;
        List<Article> articleList = new ArrayList<>();

        List<Article> articles;
        if (classify.equals("全部")) {
            articles = articleSearchMapper.searchArticleTitle(search, integer);
            articleList.addAll(articles);
            if (articles.size() < 10) {
                int remainingCount = 10 - articles.size(); // 计算还需要搜索的文章数量
                List<Article> articles1 = articleSearchMapper.searchArticleContent(search, integer, remainingCount);
                articleList.addAll(articles1); // 使用addAll方法添加articles1列表的所有元素
            }
        }else{
            articles = articleSearchMapper.searchArticleByClassifyTitle(search, integer, classify);
            articleList.addAll(articles);
            if (articles.size() < 10) {
                int remainingCount = 10 - articles.size(); // 计算还需要搜索的文章数量
                List<Article> articles1 = articleSearchMapper.searchArticleByClassifyContent(search, integer, classify, remainingCount);
                articleList.addAll(articles1); // 使用addAll方法添加articles1列表的所有元素
            }
        }
        return articleList;

    }

    @Override
    public Integer getSearchArticleCount(String search,String classify){
        if(classify.equals("全部")){
            return articleSearchMapper.getSearchArticleCount(search);
        }else{
            return articleSearchMapper.getSearchArticleClassifyCount(search, classify);
        }
    }

    @Override
    public List<String> getArticleClassifyBySearch(String search){
        return articleSearchMapper.getArticleClassifyBySearch(search);
    }


}
