package com.liugeng.rpcframework.rpcclient.network.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.function.Consumer;

import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.utils.NetworkUtil;
import io.netty.bootstrap.Bootstrap;
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
public class PooledNettyConnectionManager implements ConnectionManager<Channel> {
	
	private static final Logger log = LoggerFactory.getLogger(PooledNettyConnectionManager.class);
	
	private volatile ChannelPoolMap<InetSocketAddress, FixedChannelPool> poolMap;
	
	private final int poolSize;

	private Consumer<Channel> channelInit;

	private NioEventLoopGroup workerGroup;
	
	public PooledNettyConnectionManager(NioEventLoopGroup workerGroup, Consumer<Channel> channelInit,
		int poolSize) {
		this.workerGroup = workerGroup;
		this.poolSize = poolSize > 0 ? poolSize : 5; // default 5 connections for one address
		this.channelInit = channelInit;
	}
	
	@Override
	public Connection<Channel> getConnection(String address) {
		if (poolMap == null) {
			createNewPool();
		}
		FixedChannelPool channelPool = poolMap.get(NetworkUtil.newSocketAddress(address));
		Channel channel = null;
		try {
			channel = channelPool.acquire().get();
			return new NettyChannelConnection(channel);
		} catch (Exception e) {
			throw new RpcFrameworkException("Failed to create new connection to address:[" + address + "], please check.", e);
		}
	}

	@Override
	public void release(Connection<Channel> connection, String address) {
		FixedChannelPool channelPool = poolMap.get(NetworkUtil.newSocketAddress(address));
		if (channelPool != null && connection != null && connection.getWrappedConnection() != null) {
			channelPool.release(connection.getWrappedConnection());
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
							ChannelPipeline pipeline = ch.pipeline();
							System.out.println(pipeline);
						}
						
						@Override
						public void channelAcquired(Channel ch) throws Exception {
							
						}
						
						@Override
						public void channelCreated(Channel ch) throws Exception {
							log.debug("Channel has been created: {}", ch.id());
							channelInit.accept(ch);
						}
					}, poolSize);
				}
			};
		}
	}

	protected static class NettyChannelConnection extends Connection<Channel> {

		protected NettyChannelConnection(Channel wrappedConnection) {
			super(wrappedConnection.id().asLongText(), wrappedConnection);
		}

		@Override
		public void destroy() throws IOException {
			getWrappedConnection().close();
		}
	}

	protected Bootstrap newBootStrap() {
		return new Bootstrap().channel(NioSocketChannel.class)
				.group(workerGroup)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
	}
}
