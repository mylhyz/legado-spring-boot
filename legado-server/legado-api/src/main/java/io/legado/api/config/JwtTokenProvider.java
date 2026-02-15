package io.legado.api.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.legado.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token工具类
 */
@Slf4j
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:legado-secret-key-change-in-production}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;
    
    private SecretKey key;
    
    @PostConstruct
    public void init() {
        // 使用HS512算法生成密钥，要求密钥长度 >= 512位(64字节)
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            log.warn("配置的JWT密钥长度不足64字节(512位)，使用自动生成的安全密钥");
            key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        } else {
            key = Keys.hmacShaKeyFor(keyBytes);
        }
    }
    
    /**
     * 生成JWT Token
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token已过期: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT Token: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("格式错误的JWT Token: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT Token为空或非法: {}", e.getMessage());
        }
        return false;
    }
    
    /**
     * 解析Token
     */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 获取Token过期时间
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }
    
}
