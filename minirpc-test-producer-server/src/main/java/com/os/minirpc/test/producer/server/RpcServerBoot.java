package com.os.minirpc.test.producer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@SuppressWarnings("all")
public class RpcServerBoot {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerBoot.class);
	
	public static void main(String[] args) {
		LOGGER.debug(RpcServerBoot.class.getName()+": Start server!");
		// 加载服务端 Spring 配置
		new ClassPathXmlApplicationContext("spring-server.xml");
	}
	
}
