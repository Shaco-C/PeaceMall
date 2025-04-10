package com.peacemall.review;


import com.peacemall.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.peacemall.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class ReviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewApplication.class, args);
    }
}
