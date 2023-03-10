package com.bizzan.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer //开启配置服务 *******注意不要漏了这个注解*********
public class ConfigCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(ConfigCenterApp.class, args);
    }
}