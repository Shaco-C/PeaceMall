package com.peacemall.api.config;

import com.peacemall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public Logger.Level fullFeignLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                Long userId = UserContext.getUserId();
                String userRole = UserContext.getUserRole();
                String userInfo = userId.toString() + "," + userRole;
                if (userId != null) {
                    template.header("user-info",userInfo );
                }
            }
        };
    }

//    @Bean
//    public ItemClientFallbackFactory itemClientFallbackFactory(){
//        return new ItemClientFallbackFactory();
//    }
}
