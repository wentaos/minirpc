package com.os.minirpc.registry;

/**
 * 查找所需服务
 */
public interface ServiceDiscovery {
	/**
	 * 根据服务名称查找服务,返回服务地址
	 * @param service_name
	 * @return 服务地址
	 */
	String discoveryReturnAddr(String service_name);
}

