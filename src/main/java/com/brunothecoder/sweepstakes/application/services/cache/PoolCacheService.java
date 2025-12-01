package com.brunothecoder.sweepstakes.application.services.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PoolCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public PoolCacheService (RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void cachePoolStats(UUID poolId, BigDecimal totalAmount){
        Map<String,Object> stats = new HashMap<>();
        stats.put("totalAmount", totalAmount);
        redisTemplate.opsForHash().putAll("pool: " + poolId, stats);
    }

    public Map<Object, Object> getPoolStats(UUID poolId){
        return redisTemplate.opsForHash().entries("pool: " + poolId);
    }
}
