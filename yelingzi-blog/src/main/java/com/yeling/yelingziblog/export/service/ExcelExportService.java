package com.yeling.yelingziblog.export.service;

import com.yeling.yelingziblog.export.DTO.ExportExcelDTO;
import com.yeling.yelingziblog.export.vo.requset.ExcelExportReq;
import com.yeling.yelingziblog.export.vo.response.TableResp;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

public interface ExcelExportService {

    void exportExcel(String tableName, Integer userId);

    void createExportFile(ExportExcelDTO dto);

    void pushErrorMessage(Integer userId, String errorMsg);

    List<TableResp> getTables();
}
