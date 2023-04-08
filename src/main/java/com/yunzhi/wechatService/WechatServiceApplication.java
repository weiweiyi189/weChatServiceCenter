package com.yunzhi.wechatService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class WechatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WechatServiceApplication.class, args);
	}

}
