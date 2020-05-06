package com.liugeng.rpcframework.rpcclient.network.netty;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.utils.NetworkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 15:15
 */
public class PooledNettyConnectionManager {
	
	private static final Logger log = LoggerFactory.getLogger(PooledNettyConnectionManager.class);
	
	private volatile ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;
	
	private final int poolSize;

	private Consumer<Channel> channelInit;

	private NioEventLoopGroup workerGroup;
	
	public PooledNettyConnectionManager(Consumer<Channel> channelInit, int poolSize) {
		this.poolSize = poolSize > 0 ? poolSize : 5; // default 5 connections for one address
		this.channelInit = channelInit;
	}
	
	public synchronized void init() {
		if (workerGroup == null || workerGroup.isShutdown()) {
			this.workerGroup = new NioEventLoopGroup();
		}
		if (poolMap == null) {
			createNewPool();
		}
	}
	
	public Channel getConnection(String address) {
		checkStatus();
		FixedChannelPool channelPool = poolMap.get(NetworkUtil.newSocketAddress(address));
		try {
			return channelPool.acquire().get();
		} catch (Exception e) {
			throw new RpcFrameworkException("Failed to create new connection to address:[" + address + "], please check.", e);
		}
	}
	
	public void release(Channel connection, String address) {
		checkStatus();
		FixedChannelPool channelPool = poolMap.get(NetworkUtil.newSocketAddress(address));
		if (channelPool != null && connection != null) {
			channelPool.release(connection);
		}
	}
	
	public synchronized void destroy() {
		if (poolMap != null && poolMap instanceof AbstractChannelPoolMap) {
			((AbstractChannelPoolMap)poolMap).close();
			poolMap = null;
		}
		if (workerGroup != null && !workerGroup.isShutdown()) {
			workerGroup.shutdownGracefully();
		}
	}
	
	private void checkStatus() {
		Preconditions.checkArgument(poolMap != null && workerGroup != null && !workerGroup.isShutdown(),
			"Please init firstly.");
	}

	private void createNewPool() {
		Preconditions.checkArgument(!workerGroup.isShutdown(), "NioEventLoopGroup has been shutdown, please "
			+ "create new PooledNettyConnectionManager.");
		Bootstrap bootstrap = newBootStrap();
		poolMap = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
			@Override
			protected FixedChannelPool newPool(InetSocketAddress key) {
				return new FixedChannelPool(bootstrap.remoteAddress(key), new RpcNettyChannelPoolHandler(channelInit), poolSize);
			}
		};
	}

	private Bootstrap newBootStrap() {
		return new Bootstrap().channel(NioSocketChannel.class)
				.group(workerGroup)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
	}
	
	
	private static class RpcNettyChannelPoolHandler implements ChannelPoolHandler {
		
		private Consumer<Channel> channelInit;
		
		RpcNettyChannelPoolHandler(Consumer<Channel> channelInit) {
			this.channelInit = channelInit;
		}
		
		@Override
		public void channelReleased(Channel channel) throws Exception {
			
		}
		
		@Override
		public void channelAcquired(Channel channel) throws Exception {
			
		}
		
		@Override
		public void channelCreated(Channel channel) throws Exception {
			log.debug("Channel has been created: {}", channel.id());
			channelInit.accept(channel);
		}
	}
}
