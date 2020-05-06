package com.liugeng.rpcframework.rpcclient.network.netty;

import java.util.function.Consumer;

import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 16:31
 */
public class NettyChannelInit implements Consumer<Channel> {
	
	private ResponseHolder responseHolder;
	
	public NettyChannelInit(ResponseHolder responseHolder) {
		this.responseHolder = responseHolder;
	}
	
	@Override
	public void accept(Channel channel) {
		channel
			.pipeline()
			.addLast(new RpcCodecHandler())
			.addLast(new SimpleChannelInboundHandler<RpcResponsePacket>() {
				@Override
				protected void channelRead0(ChannelHandlerContext ctx, RpcResponsePacket packet) {
					responseHolder.addResponse(packet);
				}
			});
	}
}
