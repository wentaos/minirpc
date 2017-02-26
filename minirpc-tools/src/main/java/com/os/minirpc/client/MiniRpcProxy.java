package com.os.minirpc.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.os.minirpc.common.bean.RpcRequest;
import com.os.minirpc.common.bean.RpcResponse;
import com.os.minirpc.registry.ServiceDiscovery;

/**
 * RPC 服务代理
 *
 */
public class MiniRpcProxy {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MiniRpcProxy.class);
	
	private String serviceAddress;
	
	private ServiceDiscovery serviceDiscovery;
	
	public MiniRpcProxy(String serviceAddress){
		this.serviceAddress = serviceAddress;
	}
	
	public MiniRpcProxy(ServiceDiscovery serviceDiscovery){
		this.serviceDiscovery = serviceDiscovery;
	}
	
	public <T> T create(final Class<?> interfaceClass){
		return create(interfaceClass,"");
	}
	
	@SuppressWarnings("unchecked")
	public <T> T create(final Class<?> interfaceClass,final String serviceVersion){
		return (T) Proxy.newProxyInstance(
			interfaceClass.getClassLoader(),
			new Class<?>[]{interfaceClass},
			new InvocationHandler(){

				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					// 封装请求
					RpcRequest request = new RpcRequest();
					request.setRequestId(UUID.randomUUID().toString());
					request.setInterfaceName(method.getDeclaringClass().getName());
					request.setServiceVersion(serviceVersion);
					request.setMethodName(method.getName());
					request.setParameterTypes(method.getParameterTypes());
					request.setParameters(args);
					
					if(serviceDiscovery != null){
						String serviceName = interfaceClass.getName();
						if(StringUtils.isNoneEmpty(serviceVersion)){
							serviceName += "-"+serviceVersion;
						}
						serviceAddress = serviceDiscovery.discoveryReturnAddr(serviceName);
						LOGGER.debug("discovery service: {} => {}",serviceName,serviceAddress);
					}
					
					if(StringUtils.isEmpty(serviceAddress)){
						throw new RuntimeException("service address is empty");
					}
					
					// 从RPC 服务地址中得到  host 和 port
					String[] arr = StringUtils.split(serviceAddress,":");
					
					String host = arr[0];
					int port = Integer.parseInt(arr[1]);
					
					// 发送请求
					MiniRpcClient rpcClient = new MiniRpcClient(host,port);
					RpcResponse response = rpcClient.send(request);
					
					if(response == null){
						throw new RuntimeException("send return: response is null");
					}
					
					if(response.getException()!=null){
						throw response.getException();
					} else {
						return response.getResult();
					}
					
				}
				
			});
		
	}

}
