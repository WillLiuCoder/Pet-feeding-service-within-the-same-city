package com.jinxiu.service.petauth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Component
@Slf4j
public class JwtUtil {
    
    @Value("${jwt.secret:defaultSecretKey}")
    private String secret;
    
    @Value("${jwt.expiration:86400}")
    private long expiration;
    
    @Value("${jwt.header:Authorization}")
    private String header;
    
    /**
     * 生成JWT token
     */
    public String generateToken(String username, Long userId, String roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("roles", roles);
        claims.put("created", new Date());
        
        return createToken(claims, username);
    }
    
    /**
     * 创建token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        SecretKey key = getSigningKey();
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.error("从token中获取用户名失败", e);
            return null;
        }
    }
    
    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            log.error("从token中获取用户ID失败", e);
            return null;
        }
    }
    
    /**
     * 从token中获取角色
     */
    public String getRolesFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.get("roles", String.class);
        } catch (Exception e) {
            log.error("从token中获取角色失败", e);
            return null;
        }
    }
    
    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("验证token失败", e);
            return false;
        }
    }
    
    /**
     * 从token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        SecretKey key = getSigningKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取用于HS512的签名密钥
     * 如果配置的secret长度小于64字节，则使用SHA-512对其进行哈希，保证密钥长度至少为512位
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            try {
                MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
                keyBytes = sha512.digest(keyBytes); // 64字节
            } catch (NoSuchAlgorithmException e) {
                // 理论上不会发生，JDK内置SHA-512
                throw new IllegalStateException("SHA-512 algorithm not available", e);
            }
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 获取token过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("获取token过期时间失败", e);
            return null;
        }
    }
    
    /**
     * 检查token是否即将过期
     */
    public boolean isTokenExpiringSoon(String token, long thresholdMinutes) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            if (expiration == null) {
                return false;
            }
            
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            return timeUntilExpiration <= thresholdMinutes * 60 * 1000;
        } catch (Exception e) {
            log.error("检查token是否即将过期失败", e);
            return false;
        }
    }
}
