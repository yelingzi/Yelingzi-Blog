package com.yeling.utils;

import com.yeling.article.mapper.ArticleCategoryMapper;
import com.yeling.article.mapper.ArticleCommentMapper;
import com.yeling.article.mapper.ArticleMapper;
import com.yeling.article.mapper.ArticleTagMapper;
import com.yeling.common.domian.entity.Statistics;
import com.yeling.article.vo.response.ArticleAndTalkStatisticsResp;
import com.yeling.article.vo.response.SimpleArticleResp;
import com.yeling.common.mapper.ViewInfoMapper;
import com.yeling.other.mapper.MessageMapper;
import com.yeling.talk.mapper.TalkCommentMapper;
import com.yeling.talk.mapper.TalkMapper;
import com.yeling.talk.vo.response.TalkResp;
import com.yeling.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class StatisticsUtils {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private ArticleCategoryMapper articleCategoryMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TalkMapper talkMapper;

    @Autowired
    private ArticleCommentMapper articleCommentMapper;

    @Autowired
    private TalkCommentMapper talkCommentMapper; // 新增缺失的Mapper

    @Autowired
    private ViewInfoMapper viewInfoMapper;

    public Statistics getStatistics() {
        try {
            Statistics statistics = new Statistics();

            // 设置统计数据
            statistics.setArticleCount(articleMapper.countArticleRespList());
            statistics.setTagCount(articleTagMapper.findTagCount());
            statistics.setCategoryCount(articleCategoryMapper.findCategoryCount());
            statistics.setMessageCount(messageMapper.findMessageCountByState(2));
            statistics.setUserCount(userMapper.getCountByState(0));
            statistics.setTalkCount(talkMapper.findTalksCountByState(0));
            statistics.setCommentCount(
                    articleCommentMapper.findArticleCommentCountByState(2) +
                            talkCommentMapper.findTalkCommentCountByState(2)
            );

            List<Integer> views = viewInfoMapper.findViewsCountList();
            if (views != null && !views.isEmpty()) {
                int count = views.stream().mapToInt(Integer::intValue).sum();
                statistics.setViewCount(count);
            }

            System.out.println("统计数据处理完成！"); // 调整输出位置
            return statistics;
        } catch (Exception e) {
            System.err.println("获取统计数据时出错：" + e.getMessage());
            e.printStackTrace();
            return new Statistics();
        }
    }

    public List<ArticleAndTalkStatisticsResp> getArtAndTalkStat() {
        // 获取原始数据
        List<SimpleArticleResp> articles = articleMapper.findSimpleArticleListBySixMonth();
        List<TalkResp> talks = talkMapper.findTalkListBySixMonth();

        // 计算时间范围（半年前）
        LocalDateTime halfYearAgo = LocalDateTime.now().minusMonths(6);

        // 使用Stream处理数据
        return Stream.concat(
                        // 处理文章数据
                        articles.stream()
                                .filter(article -> article.getCreateTime().isAfter(halfYearAgo))
                                .map(article -> new ArticleAndTalkStatisticsResp(
                                        article.getId(),
                                        "文章",  // 类型标识
                                        article.getCreateTime()
                                )),

                        // 处理说说数据
                        talks.stream()
                                .filter(talk -> talk.getCreateTime().isAfter(halfYearAgo))
                                .map(talk -> new ArticleAndTalkStatisticsResp(
                                        talk.getId(),
                                        "说说",  // 类型标识
                                        talk.getCreateTime()
                                ))
                )
                // 按时间倒序排列
                .sorted(Comparator.comparing(ArticleAndTalkStatisticsResp::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

}