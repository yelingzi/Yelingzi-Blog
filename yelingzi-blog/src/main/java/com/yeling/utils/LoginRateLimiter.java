package com.yeling.utils;

import jakarta.annotation.PreDestroy;

import java.util.Map;
import java.util.concurrent.*;

public class LoginRateLimiter {
    private static final int MAX_ATTEMPTS = 5; // 允许的最大登录尝试次数
    private static final long EXPIRE_TIME = 1; // 过期时间（以小时为单位）
    private final Map<String, Integer> ipAccessCount = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService;
    private final ScheduledFuture<?> expirationTask;

    public LoginRateLimiter() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        // 立即启动一个计划任务来定期清理ipAccessCount
        expirationTask = executorService.scheduleAtFixedRate(this::clearIpAccessCount, EXPIRE_TIME, EXPIRE_TIME, TimeUnit.HOURS);
    }

    public boolean allowAccess(String ipAddress) {
        int accessCount = ipAccessCount.getOrDefault(ipAddress, 0);
        if (accessCount >= MAX_ATTEMPTS) {
            return false; // 超过了最大尝试次数
        }

        ipAccessCount.put(ipAddress, accessCount + 1);
        return true;
    }

    private void clearIpAccessCount() {
        ipAccessCount.clear(); // 清理所有IP地址的登录尝试次数
    }

    // 确保在不再需要LoginRateLimiter时关闭executorService
    @PreDestroy
    public void shutdown() {
        // 清理资源
        if (expirationTask != null) {
            expirationTask.cancel(true);
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    // 可能还需要一个方法来等待executorService的关闭
    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (executorService != null) {
            executorService.awaitTermination(timeout, unit);
        }
    }

    // 注意：在实际使用中，你可能需要在finally块中或在某个适当的关闭钩子中调用shutdown()方法
}

