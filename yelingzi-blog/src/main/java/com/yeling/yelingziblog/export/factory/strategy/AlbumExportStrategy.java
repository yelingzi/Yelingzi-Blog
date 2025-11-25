package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.album.entity.Album;
import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("album")
public class AlbumExportStrategy implements ExportStrategy{

    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "album";

    public AlbumExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportAlbum();
    }

    @Override
    public Class<?> getDataClass() {
        return Album.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
