package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.chat.entity.Chat;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("chat")
public class ChatExportStrategy implements ExportStrategy{

    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "chat";

    public ChatExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportChat();
    }

    @Override
    public Class<?> getDataClass() {
        return Chat.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
