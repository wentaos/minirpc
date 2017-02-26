package com.os.minirpc.test.producer;

public class RpcUser {
	
	private String name;
	
	public RpcUser(){}
	
	public RpcUser(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
