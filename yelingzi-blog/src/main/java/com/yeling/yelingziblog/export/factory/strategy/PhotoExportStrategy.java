package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.album.entity.Photo;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("photo")
public class PhotoExportStrategy implements ExportStrategy{
    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "photo";

    public PhotoExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportPhoto();
    }

    @Override
    public Class<?> getDataClass() {
        return Photo.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
