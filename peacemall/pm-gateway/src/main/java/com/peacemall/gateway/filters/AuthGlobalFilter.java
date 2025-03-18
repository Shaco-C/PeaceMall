package com.peacemall.gateway.filters;

import com.peacemall.common.enums.UserRole;
import com.peacemall.common.exception.UnauthorizedException;
import com.peacemall.gateway.config.AuthProperties;
import com.peacemall.gateway.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final JwtUtils jwtUtils;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        log.info("Gateway request: {}", path);

        // 1. 判断是否是白名单路径
        if (isExclude(path)) {
            log.info("路径 {} 免登录放行", path);
            return chain.filter(exchange);
        }

        // 2. 获取 Token
        String token = request.getHeaders().getFirst("authorization");
        if (token == null || token.isEmpty()) {
            log.warn("请求 {} 未携带 Token", path);
            return unauthorizedResponse(exchange);
        }

        // 判断token是否过期
        if (!jwtUtils.verify(token)){
            log.warn("Token 已过期: {}", token);
            return unauthorizedResponse(exchange);
        }

        // 3. 校验 Token
        Long userId;
        String userRole;
        try {
            userId = jwtUtils.getUserId(token);
            userRole = jwtUtils.getUserRole(token);
        } catch (UnauthorizedException e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            return unauthorizedResponse(exchange);
        }

        // 4. 权限控制（**/admin/** 仅 ADMIN 可访问）
        if (isAdminPath(path) && !"ADMIN".equalsIgnoreCase(userRole)) {
            log.warn("用户 {} 角色 {} 访问 {} 被拒绝", userId, userRole, path);
            return forbiddenResponse(exchange);
        }

        // 5. 权限控制（**/merchant/** 仅 MERCHANT 或 ADMIN 可访问）
        if (isMerchantPath(path) && !isMerchantOrAdmin(userRole)) {
            log.warn("用户 {} 角色 {} 访问 {} 被拒绝", userId, userRole, path);
            return forbiddenResponse(exchange);
        }

        // 6. 传递用户信息
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("user-info", userId + "," + userRole))
                .build();
        return chain.filter(swe);
    }

    // 白名单路径
    private boolean isExclude(String path) {
        return authProperties.getExcludePaths()
                .stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    // **/admin/** 访问控制
    private boolean isAdminPath(String path) {
        return antPathMatcher.match("*/admin/**", path);
    }

    // **/shop/** 访问控制
    private boolean isMerchantPath(String path) {
        return antPathMatcher.match("*/merchant/**", path);
    }

    // 判断是否是商家或管理员
    private boolean isMerchantOrAdmin(String role) {
        return UserRole.MERCHANT.name().equalsIgnoreCase(role) || UserRole.ADMIN.name().equalsIgnoreCase(role);
    }

    // 401 Unauthorized
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    // 403 Forbidden
    private Mono<Void> forbiddenResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
