package com.os.minirpc.common.decode;

import com.os.minirpc.common.utils.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RPC 解码器
 *
 */
public class RpcEncoder extends MessageToByteEncoder {
	
	private Class<?> genericClass;
	
	public RpcEncoder(Class<?> genericClass){
		this.genericClass = genericClass;
	}
	
	@Override
	protected void encode(ChannelHandlerContext content, Object in, ByteBuf out) throws Exception {
		if(genericClass.isInstance(in)){
			byte[] data = SerializationUtil.serialize(in);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}
	
}
