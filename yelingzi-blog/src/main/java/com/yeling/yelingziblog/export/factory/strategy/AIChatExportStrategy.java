package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.chatai.entity.ChatAi;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("chat-ai")
public class AIChatExportStrategy implements ExportStrategy{
    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "chat-ai";

    public AIChatExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportAIChat();
    }

    @Override
    public Class<?> getDataClass() {
        return ChatAi.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
