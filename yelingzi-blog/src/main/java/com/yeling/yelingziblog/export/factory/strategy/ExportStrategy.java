package com.yeling.yelingziblog.export.factory.strategy;

import java.util.List;

public interface ExportStrategy {
    List<?> exportData();
    Class<?> getDataClass();
    boolean support(String tableName);
}
