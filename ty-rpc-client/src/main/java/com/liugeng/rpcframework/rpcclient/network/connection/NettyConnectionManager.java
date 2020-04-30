package com.liugeng.rpcframework.rpcclient.network.connection;

import java.io.IOException;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.utils.NetworkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 15:15
 */
public class NettyConnectionManager implements ConnectionManager<Channel> {
	
	private NioEventLoopGroup workerGroup;
	
	private ChannelInitializer<NioSocketChannel> channelInitializer;
	
	public NettyConnectionManager(NioEventLoopGroup workerGroup, ChannelInitializer<NioSocketChannel> channelInitializer) {
		this.workerGroup = workerGroup;
		this.channelInitializer = channelInitializer;
	}
	
	@Override
	public Connection<Channel> getConnection(String address) {
		Bootstrap bootstrap = newBootStrap();
		try {
			Channel channel = bootstrap.connect(NetworkUtil.newSocketAddress(address)).sync().channel();
			return new NettyChannelConnection(channel);
		} catch (InterruptedException e) {
			throw new RpcFrameworkException("exception during connect to rpc server", e);
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
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
			.handler(channelInitializer);
	}
}
