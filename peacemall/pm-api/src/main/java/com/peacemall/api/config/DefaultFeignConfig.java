package com.peacemall.api.config;

import com.peacemall.api.client.fallback.ProductClientFallbackFactory;
import com.peacemall.api.client.fallback.ShopClientFallbackFactory;
import com.peacemall.api.client.fallback.UserClientFallbackFactory;
import com.peacemall.api.client.fallback.WalletClientFallbackFacktory;
import com.peacemall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

/**
 * @author watergun
 */
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
                String userInfo = userId + "," + userRole;
                if (userId != null) {
                    template.header("user-info", userInfo);
                }
            }
        };
    }


    @Bean
    public WalletClientFallbackFacktory walletClientFallbackFacktory(){
        return new WalletClientFallbackFacktory();
    }

    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return new UserClientFallbackFactory();
    }

    @Bean
    public ShopClientFallbackFactory shopClientFallbackFactory(){
        return new ShopClientFallbackFactory();
    }

    @Bean
    public ProductClientFallbackFactory productClientFallbackFactory(){
        return new ProductClientFallbackFactory();
    }
}
