package com.os.minirpc.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.os.minirpc.common.bean.RpcRequest;
import com.os.minirpc.common.bean.RpcResponse;
import com.os.minirpc.common.utils.StringUtil;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

public class MiniRpcServerHandler extends SimpleChannelInboundHandler<RpcRequest>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MiniRpcServerHandler.class);
	
	private final Map<String,Object> handlerMap;
	
	public MiniRpcServerHandler(Map<String,Object> handlerMap){
		this.handlerMap = handlerMap;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext context, RpcRequest request) throws Exception {
		RpcResponse response = new RpcResponse();
		// 封装 response
		response.setRequestId(request.getRequestId());
		try{
			// 执行服务方法获取结果
			Object result = this.handler(request);
			response.setResult(result);
		}catch(Exception e){
			LOGGER.error("MiniRpcServerHandler:handler result failure",e);
			response.setException(e);
		} finally {
			// 写入 RPC 响应对象并自动关闭连接
			context.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		}
	}
	
	// 执行服务方法
	private Object handler(RpcRequest request) throws Exception {
		String serviceName = request.getInterfaceName();
		String serviceVersion = request.getServiceVersion();
		if(StringUtil.isNotEmpty(serviceVersion)){
			serviceName += "-"+serviceVersion;
		}
		
		// 获取服务对象
		Object serviceBean = handlerMap.get(serviceName);
		if(serviceBean == null){
			throw new RuntimeException(String.format("Can not find service bean by key：%s", serviceName));
		}
		
		// 获取request封装的服务信息:方法名、参数、参数类型
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters  =request.getParameters();
		
		// 使用CGLib反射调用方法
		Class<?> serviceClass = serviceBean.getClass();
		FastClass serviceFastClass = FastClass.create(serviceClass);
		FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName,parameterTypes);
		// 返回执行结果
		return serviceFastMethod.invoke(serviceBean, parameters);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }

}
