package com.yeling.common.service;

import com.yeling.common.domian.entity.Statistics;
import com.yeling.user.domian.entity.User;
import com.yeling.common.domian.entity.Views;
import com.yeling.article.vo.response.ArticleAndTalkStatisticsResp;
import com.yeling.common.vo.response.ViewInfoResp;

import java.util.List;

public interface StatisticsService {

    Statistics getStatistics();

    List<ArticleAndTalkStatisticsResp> getArticleAndTalkStatistics();

    List<Views> getViewsStatistics();

    void addViewsInfo(String ip);

    void addViewsInfoByUser(String ip, User user);

    List<ViewInfoResp> getViewInfoList();

    void testAdd(String date);

}
