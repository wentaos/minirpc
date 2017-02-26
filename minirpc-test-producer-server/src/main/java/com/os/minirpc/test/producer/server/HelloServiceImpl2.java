package com.os.minirpc.test.producer.server;

import com.os.minirpc.server.WillRegService;
import com.os.minirpc.test.producer.HelloService;
import com.os.minirpc.test.producer.RpcUser;

@WillRegService(value=HelloService.class,version="mini.service2")
public class HelloServiceImpl2 implements HelloService{

	@Override
	public String hello(String name) {
		return "你好 "+name;
	}

	@Override
	public String hello(RpcUser user) {
		return "你好 "+user.getName();
	}

}
