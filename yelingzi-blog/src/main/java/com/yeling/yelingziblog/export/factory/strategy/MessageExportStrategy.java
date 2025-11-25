package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import com.yeling.yelingziblog.other.entity.Message;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("message")
public class MessageExportStrategy implements ExportStrategy {

    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "message";

    public MessageExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportMessage();
    }

    @Override
    public Class<?> getDataClass() {
        return Message.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
