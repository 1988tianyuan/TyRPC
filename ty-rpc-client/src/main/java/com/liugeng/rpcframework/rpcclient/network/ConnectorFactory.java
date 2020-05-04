package com.liugeng.rpcframework.rpcclient.network;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.collections4.MapUtils;

import com.liugeng.rpcframework.rpcclient.network.connection.ConnectionManager;
import com.liugeng.rpcframework.rpcclient.network.connection.PooledNettyConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:57
 */
public class ConnectorFactory {
	
	public static NetworkConnector newNettyConnector(Map<String, Object> connectConfig) {
		ConnectionManager<Channel> connectionManager;
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		ResponseHolder responseHolder = new ResponseHolder();
		int poolSize = MapUtils.getIntValue(connectConfig, NettyConnector.POOL_SIZE, 0);
		connectionManager = new PooledNettyConnectionManager(workerGroup, new ChannelInit(responseHolder),
				poolSize);
		return new NettyConnector(workerGroup, connectionManager, responseHolder);
	}
	
	private static class ChannelInit implements Consumer<Channel> {

		private final ResponseHolder responseHolder;

		public ChannelInit(ResponseHolder responseHolder) {
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
					System.out.println("拿到response了！！！");
					responseHolder.addResponse(packet);
				}
			});
		}
	}
}
