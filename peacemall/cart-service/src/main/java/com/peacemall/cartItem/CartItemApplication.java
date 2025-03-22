package com.peacemall.cartItem;

import com.peacemall.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.peacemall.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class CartItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CartItemApplication.class, args);
    }
}
