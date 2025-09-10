package com.yeling.common.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface RedisService {

    void set(String key, Object value);

    void setWithExpire(String key, Object value, long timeout, TimeUnit unit);

    void hset(String key, String field, Object value);

    void hmset(String key, Map<String, Object> map);

    Object get(String key);

    Object hget(String key, String field);

    boolean delete(String key);

    <T> Map<String, T> hgetAll(String key, Class<T> type);

    void expire(String key, long timeout, TimeUnit unit);

    long increment(String key, long delta);

}
