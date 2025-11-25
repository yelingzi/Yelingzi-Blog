package com.yeling.yelingziblog.export.factory;

import com.yeling.yelingziblog.common.exception.BaseException;
import com.yeling.yelingziblog.export.config.ExportTable;
import com.yeling.yelingziblog.export.config.ExportTableProperties;
import com.yeling.yelingziblog.export.factory.strategy.ExportStrategy;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExportStrategyFactory {

    private final List<ExportStrategy> strategies;
    private final ExportTableProperties properties;

    // 表名 -> 策略的映射
    private final Map<String, ExportStrategy> strategyMap = new ConcurrentHashMap<>();

    // 表名 -> 配置的映射（只包含有策略的实现）
    private final Map<String, ExportTableProperties.TableConfig> configMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void afterPropertiesSet() {
        if (properties.getTables() == null) {
            log.warn("export.tables 配置为空");
            return;
        }

        strategies.forEach(strategy -> {
            ExportTable annotation = strategy.getClass().getAnnotation(ExportTable.class);
            if (annotation != null) {
                strategyMap.put(annotation.value(), strategy);
            }
        });

        // 1. 建立策略映射
        strategies.forEach(strategy -> {
            String tableName = getTableNameFromStrategy(strategy);
            strategyMap.put(tableName, strategy);
        });

        // 2. 验证配置：只保留有策略实现的配置
        properties.getTables().forEach(config -> {
            String tableName = config.getName();
            if (strategyMap.containsKey(tableName)) {
                configMap.put(tableName, config);
            } else {
                log.warn("配置中的表 {} 无对应策略实现，已忽略", tableName);
            }
        });

        log.info("加载导出配置完成: {} 个表", configMap.size());
    }

    // 从策略类提取表名（支持注解或接口方法）
    private String getTableNameFromStrategy(ExportStrategy strategy) {
        // 方式1：通过support方法反向获取（推荐）
        // 在策略类中定义常量 TABLE_NAME
        return strategy.getClass().getAnnotation(ExportTable.class).value();
    }

    /**
     * 获取策略（带启用状态检查）
     */
    public ExportStrategy getStrategy(String tableName) {
        ExportTableProperties.TableConfig config = configMap.get(tableName);
        if (config == null) {
            throw new BaseException(404, "不支持的表名: " + tableName); // 明确表不存在
        }
        if (!config.isEnabled()) {
            throw new BaseException(403, "表已禁用: " + tableName); // 明确表禁用
        }

        ExportStrategy strategy = strategyMap.get(tableName);
        if (strategy == null) {
            throw new BaseException(500, "策略初始化异常: " + tableName); // 策略未初始化
        }
        return strategy;
    }

    /**
     * 获取所有可用的导出表配置（给前端用）
     */
    public List<ExportTableProperties.TableConfig> getAvailableTables() {
        return configMap.values().stream()
                .filter(ExportTableProperties.TableConfig::isEnabled)
                .collect(Collectors.toList());
    }

}
