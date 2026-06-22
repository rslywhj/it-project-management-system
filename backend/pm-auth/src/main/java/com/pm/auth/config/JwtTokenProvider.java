package com.pm.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token 生成/解析/校验
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration:7200000}")
    private long accessTokenExpiration; // 2h

    @Value("${jwt.refresh-token-expiration:604800000}")
    private long refreshTokenExpiration; // 7d

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isEmpty() || secret.contains("default")) {
            throw new IllegalArgumentException(
                    "JWT_SECRET 环境变量未设置或使用了不安全的默认值。" +
                    "请通过环境变量 JWT_SECRET 设置一个安全的密钥（至少256位）。");
        }
        log.info("JWT密钥已从环境变量加载");
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Access Token
     */
    public String generateAccessToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        claims.put("type", "access");
        return createToken(claims, username, accessTokenExpiration);
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshTokenExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 有效性（包括黑名单检查）
     */
    public boolean validateToken(String token) {
        try {
            // 检查Token是否在黑名单中
            if (isTokenBlacklisted(token)) {
                log.warn("Token已被撤销（在黑名单中）");
                return false;
            }
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired");
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 检查Token是否在黑名单中
     */
    private boolean isTokenBlacklisted(String token) {
        if (stringRedisTemplate == null) {
            return false;
        }
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(TOKEN_BLACKLIST_PREFIX + token));
    }

    /**
     * 从 Token 中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 获取 Token 过期时间
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
