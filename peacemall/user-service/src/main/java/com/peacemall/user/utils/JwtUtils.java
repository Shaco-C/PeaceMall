package com.peacemall.user.utils;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.RegisteredPayload;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类（基于 Hutool）
 */
@Component
public class JwtUtils {

    private final JWTSigner signer;
    private final long expire;

    public JwtUtils(@Value("${pm.jwt.secret}") String secretKey,
                    @Value("${pm.jwt.expire}") long expire) {
        this.signer = JWTSignerUtil.hs256(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expire = expire;
    }

    /**
     * 生成 Token
     *
     * @param userId   用户 ID
     * @param userRole 用户角色
     * @return JWT token
     */
    public String createToken(Long userId, String userRole) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userRole", userRole);
        payload.put(RegisteredPayload.ISSUED_AT, System.currentTimeMillis());
        payload.put(RegisteredPayload.EXPIRES_AT, System.currentTimeMillis() + expire);

        return JWTUtil.createToken(payload, signer);
    }

    /**
     * 解析 Token 获取用户 ID
     *
     * @param token JWT token
     * @return 用户 ID
     */
    public Long getUserId(String token) {
        return (Long) parseToken(token).getPayload("userId");
    }

    /**
     * 解析 Token 获取用户角色
     *
     * @param token JWT token
     * @return 用户角色
     */
    public String getUserRole(String token) {
        return parseToken(token).getPayload("userRole").toString();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT token
     * @return 是否有效
     */
    public boolean verify(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            JWTValidator.of(jwt).validateAlgorithm(signer).validateDate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析 Token
     *
     * @param token JWT token
     * @return JWT 对象
     */
    private JWT parseToken(String token) {
        return JWTUtil.parseToken(token);
    }
}
