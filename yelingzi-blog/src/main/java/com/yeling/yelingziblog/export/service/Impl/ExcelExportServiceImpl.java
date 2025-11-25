package com.yeling.yelingziblog.export.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.yeling.yelingziblog.common.dto.NettyPushMessage;
import com.yeling.yelingziblog.common.exception.BaseException;
import com.yeling.yelingziblog.common.utils.WsPushUtil;
import com.yeling.yelingziblog.export.DTO.ExportExcelDTO;
import com.yeling.yelingziblog.export.config.ExportTableProperties;
import com.yeling.yelingziblog.export.factory.ExportStrategyFactory;
import com.yeling.yelingziblog.export.factory.strategy.ExportStrategy;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import com.yeling.yelingziblog.export.service.ExcelExportService;
import com.yeling.yelingziblog.export.vo.response.ExportDownloadResp;
import com.yeling.yelingziblog.export.vo.response.TableResp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Value("${file.export.save-path}")
    private String tempPath;

    @Value("${file.export.relative-path}")
    private String urlPrefix;

    private final ExcelExportMapper excelExportMapper;
    private final RabbitTemplate rabbitTemplate;
    private final ExportStrategyFactory strategyFactory;

    public ExcelExportServiceImpl(ExcelExportMapper excelExportMapper,
                                  RabbitTemplate rabbitTemplate,
                                  ExportStrategyFactory exportStrategyFactory) {
        this.excelExportMapper = excelExportMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.strategyFactory = exportStrategyFactory;
    }
    @Override
    public void exportExcel(String tableName, Integer userId) {

        try {
            // 1. 只验证策略是否存在，不查询数据
            ExportStrategy strategy = strategyFactory.getStrategy(tableName);

            // 2. 发送MQ消息（携带Class信息即可）
            ExportExcelDTO dto = new ExportExcelDTO(
                    tableName, userId, strategy.getDataClass()
            );
            rabbitTemplate.convertAndSend("export.exchange", "export.excel", dto);

            log.info("导出任务已提交: table={}, user={}", tableName, userId);

        } catch (BaseException e) {
            // 策略相关的异常（表不存在、表禁用等）
            log.error("导出策略验证失败: table={}, user={}", tableName, userId, e);
            pushErrorMessage(userId, "导出配置错误: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            // MQ相关的异常
            log.error("MQ消息发送失败: table={}, user={}", tableName, userId, e);
            pushErrorMessage(userId, "系统繁忙，请稍后重试");
            throw new BaseException(500, "系统繁忙，请稍后重试");
        }
    }

    @Override
    public void createExportFile(ExportExcelDTO dto) {
        if (dto == null || dto.getDataClass() == null) {
            log.error("DTO或dataClass为空: {}", dto);
            pushErrorMessage(dto.getUserId(), "导出失败：参数错误");
            return;
        }

        // 1. 查询数据（唯一一次）
        ExportStrategy strategy = strategyFactory.getStrategy(dto.getTableName());
        List<?> dataList = strategy.exportData();

        // 2. 校验数据（从第一次查询后移到这里）
        if (CollectionUtils.isEmpty(dataList)) {
            log.warn("表 {} 无数据可导出", dto.getTableName());
            pushErrorMessage(dto.getUserId(), "导出失败：数据为空");
            return;
        }

        // 3. 验证数据类型
        Object firstItem = dataList.get(0);
        if (!dto.getDataClass().isInstance(firstItem)) {
            log.error("数据类型不匹配: expected={}, actual={}",
                    dto.getDataClass(), firstItem.getClass());
            pushErrorMessage(dto.getUserId(), "数据类型错误");
            return;
        }

        log.info("准备生成Excel: user={}, table={}, class={}, size={}",
                dto.getUserId(), dto.getTableName(),
                dto.getDataClass().getSimpleName(), dataList.size());

        // 4. 生成文件名并写入Excel（保持不变）
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = dto.getTableName() + "_" + timestamp + ".xlsx";

        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // 4. 生成文件名并写入Excel
        // 使用 try-with-resources 自动关闭流
        String filePath = tempPath + fileName;
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            EasyExcel.write(outputStream, dto.getDataClass())
                    .sheet("数据表")
                    .doWrite(dataList);

            String url = urlPrefix + fileName;
            pushDownloadUrl(dto.getUserId(), url, dto.getTableName());
            log.info("Excel生成成功: user={}, url={}", dto.getUserId(), url);

        } catch (Exception e) {
            log.error("Excel生成失败", e);
            pushErrorMessage(dto.getUserId(), "文件生成失败: " + e.getMessage());

            // 异常时删除可能生成的空文件
            File failedFile = new File(filePath);
            if (failedFile.exists()) {
                failedFile.delete();
            }
        }
    }
    private void pushDownloadUrl(Integer userId, String url, String tableName) {
        try {
            NettyPushMessage pushMsg = new NettyPushMessage();
            pushMsg.setMessageType("export");
            ExportDownloadResp resp = new ExportDownloadResp(url, tableName);
            pushMsg.setData(resp);
            pushMsg.setStatus("success");

            WsPushUtil.push(userId.toString(), pushMsg);
        } catch (Exception e) {
            log.error("WebSocket推送失败: user={}", userId, e);
        }
    }
    @Override
    public void pushErrorMessage(Integer userId, String errorMsg) {
        try {
            NettyPushMessage pushMsg = new NettyPushMessage();
            pushMsg.setMessageType("export");
            pushMsg.setStatus("error");
            pushMsg.setData(errorMsg);

            WsPushUtil.push(userId.toString(), pushMsg);
        } catch (Exception e) {
            log.error("WebSocket推送失败: user={}", userId, e);
        }
    }

    @Override
    public List<TableResp> getTables(){
        // 1. 获取所有启用的配置
        List<ExportTableProperties.TableConfig> configs = strategyFactory.getAvailableTables();

        log.info("获取所有可用的导出表: {} 个", configs.size());

        return configs.stream()
                .map(config -> new TableResp(
                        config.getName(),
                        config.getDescription()
                ))
                .collect(Collectors.toList());
    }

}
