package com.yeling.yelingziblog.export.controller;

import com.yeling.yelingziblog.chatai.vo.request.AppAiChatReq;
import com.yeling.yelingziblog.common.dto.ApiResponse;
import com.yeling.yelingziblog.common.dto.UserContext;
import com.yeling.yelingziblog.common.utils.IpUtils;
import com.yeling.yelingziblog.common.utils.JwtUtils;
import com.yeling.yelingziblog.export.config.ExportTableProperties;
import com.yeling.yelingziblog.export.service.ExcelExportService;
import com.yeling.yelingziblog.export.vo.requset.ExcelExportReq;
import com.yeling.yelingziblog.export.vo.response.TableResp;
import com.yeling.yelingziblog.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RestControllerAdvice
@RequestMapping("/api/admin/system/export")
public class ExcelExportController {

    private final ExcelExportService excelExportService;

    public ExcelExportController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @PostMapping(value = "/excel")
    public ApiResponse appAiChat(@RequestBody ExcelExportReq excelExportReq) {

        User user = UserContext.getUser();

        excelExportService.exportExcel(excelExportReq.getTableName(), user.getId());

        return ApiResponse.success();
    }

    @GetMapping("/tables")
    public ApiResponse getExportableTables() {

        List<TableResp> result = excelExportService.getTables();

        return ApiResponse.success(result);
    }


}
