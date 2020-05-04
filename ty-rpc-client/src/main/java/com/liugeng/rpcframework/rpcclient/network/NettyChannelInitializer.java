package com.liugeng.rpcframework.rpcclient.network;

import java.util.Optional;
import java.util.function.Consumer;

import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 17:33
 */
public class NettyChannelInitializer extends ChannelInitializer<NioSocketChannel> {
	
	private Consumer<RpcResponsePacket> responseConsumer;

	@Override
	protected void initChannel(NioSocketChannel channel) throws Exception {
		channel.pipeline()
			.addLast(new RpcCodecHandler())
			.addLast(new SimpleChannelInboundHandler<RpcResponsePacket>() {
				@Override
				protected void channelRead0(ChannelHandlerContext ctx, RpcResponsePacket packet) throws Exception {
					Optional.of(responseConsumer).ifPresent(consumer -> consumer.accept(packet));
				}
			});
	}

	public void setResponseConsumer(Consumer<RpcResponsePacket> responseConsumer) {
		this.responseConsumer = responseConsumer;
	}
}
