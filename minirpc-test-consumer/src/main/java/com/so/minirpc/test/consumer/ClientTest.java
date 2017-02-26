package com.so.minirpc.test.consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.os.minirpc.client.MiniRpcProxy;
import com.os.minirpc.test.producer.HelloService;
import com.os.minirpc.test.producer.RpcUser;

public class ClientTest {
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
		MiniRpcProxy rpcProxy = (MiniRpcProxy)context.getBean("rpcProxy");
		HelloService serviceBean1 = rpcProxy.create(HelloService.class);
		String result = serviceBean1.hello("Wator");
		System.out.println(result);
		
		HelloService serviceBean2 = rpcProxy.create(HelloService.class,"mini.service2");
		result = serviceBean2.hello(new RpcUser("宋文滔"));
		System.out.println(result);
		
		System.exit(0);
	}
	
}
