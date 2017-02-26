package com.os.minirpc.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.os.minirpc.common.bean.RpcRequest;
import com.os.minirpc.common.bean.RpcResponse;
import com.os.minirpc.common.decode.RpcDecoder;
import com.os.minirpc.common.decode.RpcEncoder;
import com.os.minirpc.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MiniRpcServer implements ApplicationContextAware,InitializingBean {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MiniRpcServer.class);
	
	// 注册的服务地址
	private String serviceAddress;
	
	// 调用 registry注册方法的接口
	private ServiceRegistry serviceRegistry;
	
	// 存放服务名与服务实例 之间的对应关系
	private Map<String,Object> handlerMap = new HashMap<String,Object>();
	
	public MiniRpcServer(String serviceAddress){
		this.serviceAddress = serviceAddress;
	}
	
	public MiniRpcServer(String serviceAddress,ServiceRegistry serviceRegistry){
		this.serviceAddress = serviceAddress;
		this.serviceRegistry = serviceRegistry;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// 扫描使用了 WillRegService注解的服务
		Map<String,Object> serviceBeanMap = context.getBeansWithAnnotation(WillRegService.class);
		if(MapUtils.isNotEmpty(serviceBeanMap)) {
			for(Object serviceBean : serviceBeanMap.values()){
				// 获取注解，得到服务版本
				WillRegService anno = serviceBean.getClass().getAnnotation(WillRegService.class);
				String serviceName = anno.value().getName();
				String serviceVersion = anno.version();
				// 如果存在版本号：追加服务名称
				if(StringUtils.isNotEmpty(serviceVersion)){
					serviceName += "-"+serviceVersion;
				}
				// 初始化 handlerMap
				handlerMap.put(serviceName, serviceBean);
			}
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		
		try{
			// 创建并初始化 Netty 服务器 Bootstrap 对象
			ServerBootstrap bootstrap = new ServerBootstrap();
			
			bootstrap.group(bossGroup,workGroup)
					 .channel(NioServerSocketChannel.class)
					 .childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.addLast(new RpcDecoder(RpcRequest.class));
							pipeline.addLast(new RpcEncoder(RpcResponse.class));
							// 这里的 handlerMap 在前面的方法中已经被初始化
							pipeline.addLast(new MiniRpcServerHandler(handlerMap));
						}
						 
					});
			
			bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			// 得到服务注册的ip和port
			String[] addressArr = StringUtils.split(serviceAddress,":");
			String ip = addressArr[0];
			int port = Integer.parseInt(addressArr[1]);
			
			// 启动注册服务
			ChannelFuture channelFuture  =bootstrap.bind(ip,port).sync();
			
			// 注册服务
			if(serviceRegistry != null){
				for(String serviceName : handlerMap.keySet()){
					// 调用注册方法
					serviceRegistry.registry(serviceName, serviceAddress);
					LOGGER.debug("register service :{} to {}",serviceName,serviceAddress);
				}
			}
			LOGGER.debug("server started on port {}",port);
			
			// 关闭使用完的注册服务
			channelFuture.channel().closeFuture().sync();
			
		}catch(Exception e){
			LOGGER.error("registry server failure",e);
		}finally{
			workGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}

}
