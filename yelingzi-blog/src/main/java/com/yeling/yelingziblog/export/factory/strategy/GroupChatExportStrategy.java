package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.chat.entity.GroupChat;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("group-chat")
public class GroupChatExportStrategy implements ExportStrategy{
    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "group-chat";

    public GroupChatExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportGroupChat();
    }

    @Override
    public Class<?> getDataClass() {
        return GroupChat.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
