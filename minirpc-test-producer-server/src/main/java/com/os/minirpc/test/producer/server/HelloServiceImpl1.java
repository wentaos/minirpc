package com.os.minirpc.test.producer.server;

import com.os.minirpc.server.WillRegService;
import com.os.minirpc.test.producer.HelloService;
import com.os.minirpc.test.producer.RpcUser;

@WillRegService(HelloService.class)
public class HelloServiceImpl1 implements HelloService{

	@Override
	public String hello(String name) {
		return "Hello "+name;
	}

	@Override
	public String hello(RpcUser user) {
		return "Hello "+user.getName();
	}

}
