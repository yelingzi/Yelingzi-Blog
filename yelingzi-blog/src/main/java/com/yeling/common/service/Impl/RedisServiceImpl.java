package com.yeling.common.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.yeling.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements RedisService {


    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
        log.info("Redis set key: {}, value: {}", key, value);
    }

    @Override
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        log.info("Redis set key: {}, value: {}, expire: {} {}", key, value, timeout, unit);
    }

    @Override
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
        log.info("Redis hset key: {}, field: {}, value: {}", key, field, value);
    }

    @Override
    public void hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
        log.info("Redis hmset key: {}, map: {}", key, map);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Object hget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public <T> Map<String, T> hgetAll(String key, Class<T> type) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        Map<String, T> result = new LinkedHashMap<>();

        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            String field = (String) entry.getKey();
            T value;

            if (type.isInstance(entry.getValue())) {
                value = type.cast(entry.getValue());
            } else {
                value = JSON.parseObject(JSON.toJSONString(entry.getValue()), type);
            }

            result.put(field, value);
        }

        return result;
    }

    @Override
    public boolean delete(String key) {
        Boolean result = redisTemplate.delete(key);
        log.info("Redis delete key: {}, result: {}", key, result);
        return result != null && result;
    }

    @Override
    public void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public long increment(String key, long delta) {
        Long value = redisTemplate.opsForValue().increment(key, delta);
        return value != null ? value : 0L;
    }


}
