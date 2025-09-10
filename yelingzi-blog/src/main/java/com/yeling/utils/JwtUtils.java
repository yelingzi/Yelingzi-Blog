package com.yeling.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeling.user.domian.entity.User;
import com.yeling.exception.JwtExpiredException;
import com.yeling.exception.JwtInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {

    @Value("${file.jwtKeyPath}")
    private String filePath;

    private static SecretKey SECRET_KEY;
    private static final Long SHORT_EXPIRE = 600_000L; // 10分钟 (600,000毫秒)
    private static final Long LONG_EXPIRE = 604_800_000L; // 7天 (604,800,000毫秒)

    // 使用@PostConstruct确保在依赖注入后初始化密钥
    @PostConstruct
    private void init() {
        try {
            log.info("尝试加载JWT密钥文件: {}", filePath);
            Path keyPath = Paths.get(filePath);

            if (!Files.exists(keyPath)) {
                log.error("JWT密钥文件不存在: {}", filePath);
                throw new RuntimeException("JWT密钥文件不存在");
            }

            if (!Files.isReadable(keyPath)) {
                log.error("JWT密钥文件不可读: {}", filePath);
                throw new RuntimeException("JWT密钥文件不可读");
            }

            ObjectMapper mapper = new ObjectMapper();
            SecretKeyConfig config = mapper.readValue(keyPath.toFile(), SecretKeyConfig.class);
            log.info("成功加载JWT密钥配置: {}", config);

            byte[] decodedKey = Base64.getDecoder().decode(config.getJwtKey());
            log.debug("解码后的密钥长度: {} bytes", decodedKey.length);

            SECRET_KEY = Keys.hmacShaKeyFor(decodedKey);
            log.info("JWT密钥初始化成功");
        } catch (Exception e) {
            log.error("密钥配置加载失败", e);
            throw new RuntimeException("密钥配置加载失败", e);
        }
    }

    /**
     * 生成短期JWT令牌（10分钟有效期）
     * @param claims JWT第二部分负载 payload 中存储的内容
     */
    public static String generateShortJwt(Map<String, Object> claims) {
        return generateJwt(claims, SHORT_EXPIRE);
    }

    /**
     * 生成长期JWT令牌（7天有效期）
     * @param claims JWT第二部分负载 payload 中存储的内容
     */
    public static String generateLongJwt(Map<String, Object> claims) {
        return generateJwt(claims, LONG_EXPIRE);
    }

    /**
     * 通用JWT生成方法
     * @param claims JWT负载内容
     * @param expireTime 过期时间（毫秒）
     */
    private static String generateJwt(Map<String, Object> claims, Long expireTime) {
        return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt) {

        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();

    }



    public static Integer getUserId(String token) {
        try{
            Claims claims = parseJWT(token);
            return claims.get("id", Integer.class);
        }catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Token已过期");
        } catch (Exception e) {
            throw new JwtInvalidException("Token无效");
        }

    }

    public static Integer getUserIdByRefresh(String token) {
        try{
            Claims claims = parseJWT(token);
            return claims.get("id", Integer.class);
        }catch (ExpiredJwtException e) {
            throw new JwtInvalidException("Token无效");
        } catch (Exception e) {
            throw new JwtInvalidException("Token无效");
        }

    }


    public static User getUser(String token) {
        try{
            Claims claims = parseJWT(token);
            User user = new User();
            user.setId(claims.get("id", Integer.class));
            user.setNickname(claims.get("nickname", String.class));
            user.setUserAvatar(claims.get("userAvatar", String.class));
            user.setEmail(claims.get("email", String.class));
            return user;
        }catch (ExpiredJwtException e) {
            throw new JwtExpiredException("Token已过期");
        } catch (Exception e) {
            throw new JwtInvalidException("Token无效");
        }

    }



    // JSON配置类
    static class SecretKeyConfig {
        private String jwtKey;

        public String getJwtKey() {
            return jwtKey;
        }

        public void setJwtKey(String jwtKey) {
            this.jwtKey = jwtKey;
        }
    }

}
