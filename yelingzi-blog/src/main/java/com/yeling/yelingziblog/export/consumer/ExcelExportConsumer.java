package com.yeling.yelingziblog.export.consumer;

import com.yeling.yelingziblog.chatai.entity.ChatAi;
import com.yeling.yelingziblog.chatai.service.HistoryService;
import com.yeling.yelingziblog.export.DTO.ExportExcelDTO;
import com.yeling.yelingziblog.export.service.ExcelExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelExportConsumer {

    private final ExcelExportService excelExportService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "export.excel.queue"),
            exchange = @Exchange(name = "export.exchange"),
            key = "export.excel"
    ))
    public void exportExcel(ExportExcelDTO dto) {
        try {
            log.info("开始处理导出任务: {}", dto);
            excelExportService.createExportFile(dto);
        } catch (Exception e) {
            log.error("导出任务处理失败: {}", dto, e);
            // 错误处理：只推送通知，不再调用createExportFile
            excelExportService.pushErrorMessage(dto.getUserId(),
                    "导出任务执行失败: " + e.getMessage());
        }
    }
}
