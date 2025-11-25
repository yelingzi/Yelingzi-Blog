package com.yeling.yelingziblog.export.factory.strategy;

import com.yeling.yelingziblog.article.entity.Article;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.mapper.ExcelExportMapper;
import com.yeling.yelingziblog.other.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ExportTable("article")
public class ArticleExportStrategy implements ExportStrategy{
    private final ExcelExportMapper mapper;
    private static final String TABLE_NAME = "article";

    public ArticleExportStrategy(ExcelExportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<?> exportData() {
        return mapper.exportArticle();
    }

    @Override
    public Class<?> getDataClass() {
        return Article.class;
    }

    @Override
    public boolean support(String tableName) {
        return TABLE_NAME.equals(tableName);
    }
}
