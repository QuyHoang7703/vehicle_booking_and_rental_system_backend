package com.pbl6.VehicleBookingRental.user.service.impl;

import com.pbl6.VehicleBookingRental.user.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl<K, F, V> implements RedisService<K, F, V> {
    private final RedisTemplate<K, V> redisTemplate;
    private final HashOperations<K, F, V> hashOperations;
    private  JedisPool jedisPool;

    @Override
    public void setValue(K key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setTimeToLive(K key, long timeoutInMinutes) {
        redisTemplate.expire(key, timeoutInMinutes, TimeUnit.MINUTES);
    }

    @Override
    public void setHashSet(K key, F field, V value) {
        hashOperations.put(key, field, value);
    }

    @Override
    public boolean isHashFieldExists(K key, F field) {
        return hashOperations.hasKey(key, field);
    }

    @Override
    public V getValue(K key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Map<F, V> getAllHashValues(K key) {
        return hashOperations.entries(key);
    }

    @Override
    public V getHashValue(K key, F field) {
        return hashOperations.get(key, field);
    }

    @Override
    public List<V> hashGetByFieldPrefix(K key, List<F> fieldPrefixes) {
        return List.of();
    }

    @Override
    public Set<F> getAllHashFields(K key) {
        return hashOperations.entries(key).keySet();
    }

    @Override
    public void deleteKey(K key) {
        redisTemplate.delete(key);
    }

    @Override
    public void deleteHashFile(K key, F field) {
        hashOperations.delete(key, field);
    }

    @Override
    public void deleteHashFields(K key, List<F> fields) {
        for(F field : fields) {
            hashOperations.delete(key, field);
        }
    }


    @Override
    public long incrementHashValue(K key, F field, long incrementBy) {
        return hashOperations.increment(key, field, incrementBy);
    }
    @Override
    public Object runLuaScript(String script, List<String> keys, List<String> args) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Chạy script và trả kết quả
            return jedis.eval(script, keys, args);
        }
    }

}
