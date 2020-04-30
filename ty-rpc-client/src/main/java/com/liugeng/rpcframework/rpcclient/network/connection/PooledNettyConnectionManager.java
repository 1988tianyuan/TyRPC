package com.liugeng.rpcframework.rpcclient.network.connection;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.utils.NetworkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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
public class PooledNettyConnectionManager extends NettyConnectionManager {
	
	private static final Logger log = LoggerFactory.getLogger(PooledNettyConnectionManager.class);
	
	private volatile ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;
	
	private final int poolSize;
	
	public PooledNettyConnectionManager(NioEventLoopGroup workerGroup,
		ChannelInitializer<NioSocketChannel> channelInitializer, int poolSize) {
		super(workerGroup, channelInitializer);
		this.poolSize = poolSize;
	}
	
	public PooledNettyConnectionManager(NioEventLoopGroup workerGroup,
		ChannelInitializer<NioSocketChannel> channelInitializer) {
		super(workerGroup, channelInitializer);
		this.poolSize = 5; // default 5 connections for one address
	}
	
	@Override
	public Connection<Channel> getConnection(String address) {
		if (poolMap == null) {
			createNewPool();
		}
		FixedChannelPool channelPool = poolMap.get(NetworkUtil.newSocketAddress(address));
		try {
			Channel channel = channelPool.acquire().get();
			return new NettyChannelConnection(channel); 
		} catch (Exception e) {
			throw new RpcFrameworkException("Failed to create new connection to address:[" + address + "], please check.", e);
		}
	}
	
	private synchronized void createNewPool() {
		if (poolMap == null) {
			Bootstrap bootstrap = newBootStrap();
			poolMap = new AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool>() {
				@Override
				protected FixedChannelPool newPool(InetSocketAddress key) {
					return new FixedChannelPool(bootstrap.remoteAddress(key), new ChannelPoolHandler() {
						@Override
						public void channelReleased(Channel ch) throws Exception {
							
						}
						
						@Override
						public void channelAcquired(Channel ch) throws Exception {
							
						}
						
						@Override
						public void channelCreated(Channel ch) throws Exception {
							log.debug("Channel has been created: {}", ch.id());
						}
					}, poolSize);
				}
			};
		}
	}
}
