package com.yeling.yelingziblog.export.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "export")
public class ExportTableProperties {

    private List<TableConfig> tables;

    @Data
    public static class TableConfig {
        private String name;
        private String description;
        private String permission;
        private boolean enabled = true;
    }

}
