package com.liugeng.rpcframework.rpcclient.network.netty;

import java.util.function.Consumer;

import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.codec.Spliter;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import com.liugeng.rpcframework.rpcprotocal.serializer.Serializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 16:31
 */
public class NettyChannelInit implements Consumer<Channel> {
	
	private ResponseHolder responseHolder;
	
	private Serializer serializer;
	
	public NettyChannelInit(ResponseHolder responseHolder, Serializer serializer) {
		this.responseHolder = responseHolder;
		this.serializer = serializer;
	}
	
	@Override
	public void accept(Channel channel) {
		channel
			.pipeline()
			.addLast(new Spliter())
			.addLast(new RpcCodecHandler(serializer))
			.addLast(new SimpleChannelInboundHandler<RpcResponsePacket>() {
				@Override
				protected void channelRead0(ChannelHandlerContext ctx, RpcResponsePacket packet) {
					responseHolder.addResponse(packet);
				}
			});
	}
}
