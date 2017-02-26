package com.os.minirpc.registry;

/**
 * 服务的注册接口
 */
public interface ServiceRegistry {
	/**
	 * 注册服务的名称和接口
	 * @param service_name
	 * @param service_address
	 */
	void registry(String service_name,String service_address);
}
