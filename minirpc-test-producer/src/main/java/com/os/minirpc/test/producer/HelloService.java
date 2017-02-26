package com.os.minirpc.test.producer;

public interface HelloService {
	
	String hello(String name);
	
	String hello(RpcUser user);
	
}

