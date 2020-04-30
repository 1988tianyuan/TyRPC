package com.liugeng.rpcframework.rpcclient.network;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.rpcframework.rpcclient.network.connection.Connection;
import com.liugeng.rpcframework.rpcclient.network.connection.ConnectionManager;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:34
 */
public class NettyConnector implements NetworkConnector {
	
	private static final Logger log = LoggerFactory.getLogger(NettyConnector.class); 
	
	private NioEventLoopGroup workerGroup;
	
	private ConnectionManager<Channel> connectionManageStrategy;
	
	public NettyConnector(ConnectionManager<Channel> connectionManageStrategy) {
		this(new NioEventLoopGroup(), connectionManageStrategy);
	}
	
	public NettyConnector(NioEventLoopGroup workerGroup, ConnectionManager<Channel> connectionManageStrategy) {
		this.workerGroup = workerGroup;
		this.connectionManageStrategy = connectionManageStrategy;
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public RpcFutureResponse asyncSend(String address, RpcRequestPacket request) {
		Channel channel = connectionManageStrategy.getConnection(address).getWrappedConnection();
		RpcFutureResponse futureResponse = new NettyFutureResponse(request.getRequestId());
		try {
			channel.writeAndFlush(request).sync();
		} catch (Exception e) {
			log.warn("Failed to send request to address:{}", address, e);
			futureResponse.setDone(true);
			futureResponse.setError(e);
		}
		return futureResponse;
	}
	
	private static class NettyFutureResponse extends RpcFutureResponse {
		
		public NettyFutureResponse(String requestId) {
			super(requestId);
		}
		
		@Override
		public RpcResponsePacket getResponse(long time, TimeUnit timeUnit) {
			
		}
	}
	
	@Override
	public void destroy() {
		workerGroup.shutdownGracefully();
	}
	
	public static final String USE_POOL = "usePool"; 
	
	public static final String POOL_SIZE = "poolSize"; 
}
