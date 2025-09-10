package com.yeling.common.service.Impl;

import com.yeling.common.domian.entity.Statistics;
import com.yeling.user.domian.entity.User;
import com.yeling.common.domian.entity.ViewInfo;
import com.yeling.common.domian.entity.Views;
import com.yeling.article.vo.response.ArticleAndTalkStatisticsResp;
import com.yeling.common.vo.response.ViewInfoResp;
import com.yeling.common.mapper.ViewInfoMapper;
import com.yeling.common.service.RedisService;
import com.yeling.common.service.StatisticsService;
import com.yeling.utils.IpUtils;
import com.yeling.utils.StatisticsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {


    private final RedisService redisService;


    private final StatisticsUtils statisticsUtils;

    private final ViewInfoMapper viewInfoMapper;

    @Autowired
    private IpUtils ipUtils;


    public StatisticsServiceImpl(RedisService redisService,
                                 StatisticsUtils statisticsUtils,
                                 ViewInfoMapper viewInfoMapper) {
        this.redisService = redisService;
        this.statisticsUtils = statisticsUtils;
        this.viewInfoMapper = viewInfoMapper;
    }

    @Override
    public Statistics getStatistics() {
        Object cachedObject = redisService.get("statistics");

        Statistics statistics;

        if (cachedObject == null) {
            statistics = statisticsUtils.getStatistics();
            redisService.set("statistics", statistics);
        } else {
            statistics = (Statistics) cachedObject;
        }

        return statistics;
    }

    @Override
    public List<ArticleAndTalkStatisticsResp> getArticleAndTalkStatistics() {
        return statisticsUtils.getArtAndTalkStat();
    }

    @Override
    public List<Views> getViewsStatistics() {
        return viewInfoMapper.findViewsList();
    }

    @Override
    public void addViewsInfo(String ip) {

        ViewInfo viewInfo = new ViewInfo();
        viewInfo.setIp(ip);
        viewInfo.setCity(ipUtils.searchIp(ip));
        viewInfo.setCreateTime(LocalDateTime.now());
        viewInfo.setUserId(0);
        viewInfo.setNickname("游客");

        // 2. 使用Hash结构存储到Redis
        // 每日数据按日期分组
        String todayKey = "views:" + LocalDate.now().toString();

        // 使用IP作为field，防止重复记录
        redisService.hset(todayKey, ip, viewInfo);

        // 3. 设置过期时间(保留30天数据)
        redisService.expire(todayKey, 30, TimeUnit.DAYS);

        // 4. 记录到总访问量计数器
        redisService.increment("dayView", 1);
    }

    @Override
    public void addViewsInfoByUser(String ip, User user) {
        ViewInfo viewInfo = new ViewInfo();
        viewInfo.setIp(ip);
        viewInfo.setCity(ipUtils.searchIp(ip));
        viewInfo.setCreateTime(LocalDateTime.now());
        viewInfo.setUserId(user.getId());
        viewInfo.setNickname(user.getNickname());

        // 2. 使用Hash结构存储到Redis
        // 每日数据按日期分组
        String todayKey = "views:" + LocalDate.now().toString();

        // 使用IP作为field，防止重复记录
        redisService.hset(todayKey, ip, viewInfo);

        // 3. 设置过期时间(保留30天数据)
        redisService.expire(todayKey, 30, TimeUnit.DAYS);

        // 4. 记录到总访问量计数器
        redisService.increment("dayView", 1);
    }

    @Override
    public List<ViewInfoResp> getViewInfoList() {
        List<ViewInfoResp> result = new ArrayList<>();

        // 1. 从Redis中获取最近的访客记录
        List<ViewInfo> redisViewInfos = getRecentViewInfosFromRedis(10);

        // 2. 如果Redis中不足10条，从数据库中补充
        int remainingCount = 10 - redisViewInfos.size();
        if (remainingCount > 0) {
            List<ViewInfo> dbViewInfos = getRecentViewInfosFromDatabase(remainingCount);
            redisViewInfos.addAll(dbViewInfos);
        }

        // 3. 转换为响应对象并返回
        for (ViewInfo viewInfo : redisViewInfos) {
            ViewInfoResp resp = new ViewInfoResp();
            resp.setIp(viewInfo.getIp());
            resp.setCity(viewInfo.getCity());
            resp.setCreateTime(viewInfo.getCreateTime());
            resp.setNickname(viewInfo.getNickname());
            result.add(resp);
        }

        return result;
    }

    // 从Redis中获取最近的访客记录
    private List<ViewInfo> getRecentViewInfosFromRedis(int limit) {
        List<ViewInfo> viewInfos = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 30 && viewInfos.size() < limit; i++) {
            LocalDate date = today.minusDays(i);
            String key = "views:" + date.toString();

            // 直接获取反序列化的对象
            Map<String, ViewInfo> entries = redisService.hgetAll(key, ViewInfo.class);
            if (entries == null) continue;

            for (ViewInfo viewInfo : entries.values()) {
                viewInfos.add(viewInfo);
                if (viewInfos.size() >= limit) {
                    break;
                }
            }
        }

        // 按时间倒序排序
        viewInfos.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        return viewInfos.subList(0, Math.min(viewInfos.size(), limit));
    }

    // 从数据库中获取最近的访客记录
    private List<ViewInfo> getRecentViewInfosFromDatabase(int limit) {
        // 假设有一个数据库访问层，比如JPA
        return viewInfoMapper.findViewInfoListByPage(0, limit);
    }

    @Override
    @Transactional
    public void testAdd(String date) {
        try {
            // 1. 将传入的日期字符串转换为LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate specifiedDate;
            try {
                specifiedDate = LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                log.error("传入的日期格式不正确，应为yyyy-MM-dd", e);
                return;
            }

            // 2. 保存传入日期的访问记录到数据库
            String redisKey = "views:" + specifiedDate.toString();

            // 3. 从Redis获取指定日期的所有访问记录
            Map<String, ViewInfo> viewsMap = redisService.hgetAll(redisKey, ViewInfo.class);

            if (viewsMap == null || viewsMap.isEmpty()) {
                log.info("指定日期 {} 没有访问数据需要持久化", date);
                return;
            }

            // 4. 转换为ViewInfo对象列表
            List<ViewInfo> viewInfoList = new ArrayList<>(viewsMap.values());

            log.info("准备持久化日期 {} 的访问数据，总记录数：{}", date, viewInfoList.size());

            // 5. 批量插入数据库
            if (!viewInfoList.isEmpty()) {
                int batchSize = 1000;
                for (int i = 0; i < viewInfoList.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, viewInfoList.size());
                    List<ViewInfo> batch = viewInfoList.subList(i, end);

                    log.info("正在持久化第 {} 批数据，共 {} 条记录", (i / batchSize) + 1, batch.size());

                    // 批量入库操作
                    viewInfoMapper.batchInsert(batch);

                    log.info("成功持久化日期 {} 的第 {} 批数据，{} 条记录已保存到数据库",
                            date, (i / batchSize) + 1, batch.size());
                }
            }

        } catch (Exception e) {
            log.error("访问数据持久化任务执行失败", e);
            throw new RuntimeException("访问数据持久化任务执行失败", e);
        }
    }

}