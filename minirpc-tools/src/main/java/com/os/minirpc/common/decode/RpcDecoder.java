package com.os.minirpc.common.decode;

import java.util.List;

import com.os.minirpc.common.utils.SerializationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * RPC 解码器
 * 
 */
public class RpcDecoder extends ByteToMessageDecoder {
	
	private Class<?> genericClass;
	
	public RpcDecoder(Class<?> genericClass){
		this.genericClass = genericClass;
	}
	
	@Override
	protected void decode(ChannelHandlerContext contetx, ByteBuf in, List<Object> out) throws Exception {
		if(in.readableBytes()<4){
			return;
		}
		
		in.markReaderIndex();
		
		int dataLen = in.readInt();
		if(in.readableBytes() < dataLen){
			in.resetReaderIndex();
			return;
		}
		
		byte[] data = new byte[dataLen];
		in.readBytes(data);
		out.add(SerializationUtil.deserialize(data, genericClass));
	}
	
}
