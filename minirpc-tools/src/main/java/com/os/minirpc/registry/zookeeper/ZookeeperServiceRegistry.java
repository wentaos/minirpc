package com.os.minirpc.registry.zookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.os.minirpc.registry.ServiceRegistry;

/**
 * 使用Zookeeper实现服务的注册
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);
	
	private final ZkClient zkClient;
	
	public ZookeeperServiceRegistry(String zkAddress){
		zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
		LOGGER.debug("Connection Zookeeper Success!");
	}
	
	@Override
	public void registry(String service_name, String service_address) {
		// 创建 /registry 根节点：持久的
		String registryPath = Constant.ZK_REGISTRY_PATH;
		// 每次注册服务之前，判断是否存在该根节点
		if(!zkClient.exists(registryPath)){
			// 没有则进行创建：该节点是持久节点(persistent 持久的)
			zkClient.createPersistent(registryPath);
			LOGGER.debug("Create registry node: {}",registryPath);
		}
		
		// 有了根节点后，我们来注册服务节点：基于/registry 节点
		String servicePath = registryPath+"/"+service_name;
		// 同样需要判断是否存在，不存在才注册该服务
		if(!zkClient.exists(servicePath)){
			// 创建持久节点
			zkClient.createPersistent(servicePath);
			LOGGER.debug("Create service node: {}",servicePath);
		}
		
		// 创建临时 address 节点
		String addressPath = servicePath+"/address-";
		String addressNode = zkClient.createEphemeralSequential(addressPath, service_address);
		LOGGER.debug("Create address node: {}",addressNode);
	}
	
}
