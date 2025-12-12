package com.shakhawat.rbacabac.config;

import io.github.bucket4j.*;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Getter
    public enum RateLimitType {
        LOGIN(5, Duration.ofMinutes(5)),           // 5 requests per 5 minutes
        API_GENERAL(100, Duration.ofMinutes(1)),   // 100 requests per minute
        API_WRITE(20, Duration.ofMinutes(1)),      // 20 write operations per minute
        API_READ(200, Duration.ofMinutes(1));      // 200 read operations per minute

        private final long capacity;
        private final Duration refillDuration;

        RateLimitType(long capacity, Duration refillDuration) {
            this.capacity = capacity;
            this.refillDuration = refillDuration;
        }
    }

    public Bucket resolveBucket(String key, RateLimitType limitType) {
        return cache.computeIfAbsent(key, k -> createNewBucket(limitType));
    }

    private Bucket createNewBucket(RateLimitType limitType) {
        var bandwidth = Bandwidth.builder()
                .capacity(limitType.getCapacity())
                .refillGreedy(limitType.getCapacity(), limitType.getRefillDuration())
                .build();

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    public boolean tryConsume(String key, RateLimitType limitType) {
        var bucket = resolveBucket(key, limitType);
        return bucket.tryConsume(1);
    }

    public long getAvailableTokens(String key, RateLimitType limitType) {
        var bucket = resolveBucket(key, limitType);
        return bucket.getAvailableTokens();
    }

    public void clearCache(String key) {
        cache.remove(key);
    }

    public void clearAllCache() {
        cache.clear();
    }
}
