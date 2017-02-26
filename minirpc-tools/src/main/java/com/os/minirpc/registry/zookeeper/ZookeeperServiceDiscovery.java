package com.os.minirpc.registry.zookeeper;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.os.minirpc.registry.ServiceDiscovery;

/**
 * 查找服务
 *
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);
	
	private String zkAddress;
	
	public ZookeeperServiceDiscovery(String zkAddress){
		this.zkAddress = zkAddress;
	}

	@Override 
	public String discoveryReturnAddr(String service_name) {
		
		ZkClient zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
		LOGGER.debug("Connection zookeeper!");
		
		try{
			// 获取service 节点,此节点是持久节点
			String servicePath = Constant.ZK_REGISTRY_PATH+"/"+service_name;
			if(!zkClient.exists(servicePath)){
				throw new RuntimeException(String.format("Can not find any node on path: %s ", servicePath));
			}
			// 获取在此持久节点下面的 address node
			List<String> addressList = zkClient.getChildren(servicePath);
			
			if(CollectionUtils.isEmpty(addressList)){
				throw new RuntimeException(String.format("Can not find any address node on path: %s",servicePath));
			}
			
			// 获取address 节点
			String address;
			int size = addressList.size();
			
			if(size == 1){
				// 只有一个地址，就直接获取
				address = addressList.get(0);
				LOGGER.debug("Get only address node: {}",address);
			} else {
				// 存在多个地址，随即获取其中的一个
				address = addressList.get(ThreadLocalRandom.current().nextInt(size));
				LOGGER.debug("Get random address node: {}",address);
			}
			
			String addressPath = servicePath+"/"+address;
			return zkClient.readData(addressPath);
		}catch(Exception e){
			LOGGER.error("discovery service failure",e);
			return null;
		}finally{
			zkClient.close();
		}
	}
	
}
