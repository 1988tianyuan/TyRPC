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

	private ResponseHolder responseHolder;
	
	public NettyConnector(NioEventLoopGroup workerGroup,
						  ConnectionManager<Channel> connectionManageStrategy, ResponseHolder responseHolder) {
		this.workerGroup = workerGroup;
		this.connectionManageStrategy = connectionManageStrategy;
		this.responseHolder = responseHolder;
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public RpcFutureResponse asyncSend(String address, RpcRequestPacket request) {
		RpcFutureResponse futureResponse = new RpcFutureResponse(request.getRequestId(), responseHolder);
		Connection<Channel> connection = null;
		try {
			connection = connectionManageStrategy.getConnection(address);
			connection.getWrappedConnection().writeAndFlush(request).sync();
		} catch (Exception e) {
			futureResponse.setSuccess(false);
			futureResponse.setError(e);
		}
		connectionManageStrategy.release(connection, address);
		return futureResponse;
	}
	
	@Override
	public void destroy() {
		workerGroup.shutdownGracefully();
	}
	
	public static final String USE_POOL = "usePool"; 
	
	public static final String POOL_SIZE = "poolSize"; 
}
