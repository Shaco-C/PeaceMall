package com.peacemall.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.peacemall.common.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取登录用户信息

        String userInfo = request.getHeader("user-info");
        log.info("获取用户信息{}",userInfo);
        // 2.判断是否获取了用户，如果有，存入ThreadLocal
        if (StrUtil.isNotBlank(userInfo)) {
            String[] idAndRole = userInfo.split(",");
            Long userId = Long.parseLong(idAndRole[0]);
            String userRole = idAndRole[1];
            UserContext.setUser(userId,userRole);
        }
        // 3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户
        UserContext.removeUser();
    }
}
