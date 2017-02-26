package com.os.minirpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.os.minirpc.common.bean.RpcRequest;
import com.os.minirpc.common.bean.RpcResponse;
import com.os.minirpc.common.decode.RpcDecoder;
import com.os.minirpc.common.decode.RpcEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class MiniRpcClient extends SimpleChannelInboundHandler<RpcResponse>{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MiniRpcClient.class);
	
	private final String host;
	private final int port;
	
	private RpcResponse rpcResponse;
	
	public MiniRpcClient(String host,int port){
		this.host = host;
		this.port = port;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResonse) throws Exception {
		this.rpcResponse = rpcResonse;
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("api caught exception", cause);
        ctx.close();
    }
	
	public RpcResponse send(RpcRequest rpcRequest){
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					 .channel(NioSocketChannel.class)
					 .handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.addLast(new RpcEncoder(RpcRequest.class));
							pipeline.addLast(new RpcDecoder(RpcResponse.class));
							pipeline.addLast(MiniRpcClient.this);
						}
						 
					 });
			
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			ChannelFuture future = bootstrap.connect(host, port).sync();
			
			Channel channel = future.channel();
			channel.writeAndFlush(rpcRequest).sync();
			channel.closeFuture().sync();
			
			return rpcResponse;
		}catch(Exception e){
			LOGGER.error("RpcClient send request failure",e);
		}finally{
			group.shutdownGracefully();
		}
		return null;
	}

}
