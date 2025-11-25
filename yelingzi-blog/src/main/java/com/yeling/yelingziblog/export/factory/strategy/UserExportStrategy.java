package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("user")
public class UserExportStrategy implements ExportStrategy{

    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "message";

    public UserExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportUser();
    }

    @Override
    public Class<?> getDataClass() {
        return User.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
