package com.peachmall;

import com.peacemall.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author watergun
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.peacemall.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class SearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
