package com.yeling.utils;

import com.yeling.common.domian.entity.Statistics;
import com.yeling.common.domian.entity.ViewInfo;
import com.yeling.common.domian.entity.Views;
import com.yeling.common.mapper.ViewInfoMapper;
import com.yeling.other.mapper.InnMapper;
import com.yeling.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EveryDayUpdateTask {

    private final RedisService redisService;

    @Autowired
    private InnMapper innMapper;

    @Autowired
    private StatisticsUtils statisticsUtils;



    @Autowired
    private ViewInfoMapper viewInfoMapper;

    public EveryDayUpdateTask(RedisService redisService) {
        this.redisService = redisService;
    }

    // 每天12:00执行
    @Transactional
    @Scheduled(cron = "0 0 4 * * ?")
    public void myTask() {
        System.out.println("定时任务执行时间：" + new Date());

//        List<InnLedger> ledgers = innMapper.getInnRoom();
//        Date currentDate = new Date();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        for (InnLedger ledger : ledgers) {
//            try {
//                if(ledger.getOrderNum() != null){
//                    Date endDate = dateFormat.parse(ledger.getEndTime());
//                    if (endDate.before(currentDate)) {
//                        String state = innMapper.getInnLedger(ledger.getOrderNum());
//                        if(state != null){
//                            innMapper.setInnLedger(ledger.getOrderNum(),"已完成");
//                        }
//                        ledger.setState("空");
//                        ledger.setOrderNum("");
//                        ledger.setStartTime("");
//                        ledger.setEndTime("");
//                        innMapper.checkInRoom(ledger);
//
//                    }
//                }
//            } catch (Exception e) {
//                // 处理日期转换异常
//                e.printStackTrace();
//            }
//        }

        batchInsertViewInfo();


        Statistics stats = statisticsUtils.getStatistics();

        redisService.set("statistics", stats);

    }

    private void batchInsertViewInfo() {
        try {
            // 1. 获取昨天的日期
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String yesterdayKey = "views:" + yesterday.toString();

            // 3. 从Redis获取指定日期的所有访问记录
            Map<String, ViewInfo> viewsMap = redisService.hgetAll(yesterdayKey, ViewInfo.class);


            if (viewsMap == null || viewsMap.isEmpty()) {
                log.info("日期 {} 没有访问数据需要持久化", yesterday);
                return;
            }

            // 4. 转换为ViewInfo对象列表
            List<ViewInfo> viewInfoList = new ArrayList<>(viewsMap.values());

            log.info("准备持久化日期 {} 的访问数据，总记录数：{}", yesterday, viewInfoList.size());

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
                            yesterday, (i / batchSize) + 1, batch.size());
                }
            }

            // 6. 保存统计数据
            Views views = new Views();
            views.setCreateTime(yesterday.atStartOfDay());
            views.setViewCount(viewInfoList.size());
            viewInfoMapper.addView(views);

            log.info("成功持久化日期 {} 的访问数据，总计 {} 条记录", yesterday, viewInfoList.size());

        } catch (Exception e) {
            log.error("访问数据持久化任务执行失败", e);
            throw e;
        }
    }

}
