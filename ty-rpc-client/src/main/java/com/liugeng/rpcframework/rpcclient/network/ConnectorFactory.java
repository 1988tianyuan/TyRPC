package com.liugeng.rpcframework.rpcclient.network;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.liugeng.rpcframework.rpcclient.network.connection.ConnectionManager;
import com.liugeng.rpcframework.rpcclient.network.connection.NettyConnectionManager;
import com.liugeng.rpcframework.rpcclient.network.connection.PooledNettyConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:57
 */
public class ConnectorFactory {
	
	public static NetworkConnector newNettyConnector(Map<String, String> connectConfig, ResponseHolder responseHolder) {
		ConnectionManager<Channel> connectionManager;
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		ChannelInitializer<NioSocketChannel> initializer = new NettyChannelInitializer(responseHolder);
		if (MapUtils.getBoolean(connectConfig, NettyConnector.USE_POOL, false)) {
			int poolSize = MapUtils.getIntValue(connectConfig, NettyConnector.POOL_SIZE, 0);
			connectionManager = new PooledNettyConnectionManager(workerGroup, poolSize);
		} else {
			connectionManager = new NettyConnectionManager(workerGroup);
		}
		return new NettyConnector(workerGroup, connectionManager);
	}
	
	
}
