package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import com.yeling.yelingziblog.other.entity.Friend;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("friend")
public class FriendExportStrategy implements ExportStrategy{

    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "friend";

    public FriendExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportFriend();
    }

    @Override
    public Class<?> getDataClass() {
        return Friend.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
