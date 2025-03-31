package com.bank.duty.service.impl;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class TokenBlacklistService {

    // 在生产环境中，建议使用Redis而不是内存Map
    private Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * 将令牌添加到黑名单
     * @param token 需要加入黑名单的JWT令牌
     * @param expiration 令牌的过期时间戳
     */
    public void addToBlacklist(String token, long expiration) {
        blacklistedTokens.put(token, expiration);
    }

    /**
     * 检查令牌是否在黑名单中
     * @param token 需要检查的JWT令牌
     * @return 如果在黑名单中返回true，否则返回false
     */
    public boolean isBlacklisted(String token) {
        // 空值检查
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 检查是否存在于黑名单
        if (!blacklistedTokens.containsKey(token)) {
            return false;
        }

        // 检查是否已过期（过期的可以从黑名单中移除）
        long expiration = blacklistedTokens.get(token);
        if (System.currentTimeMillis() > expiration) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }

    /**
     * 定期清理过期的令牌
     */
    public void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}