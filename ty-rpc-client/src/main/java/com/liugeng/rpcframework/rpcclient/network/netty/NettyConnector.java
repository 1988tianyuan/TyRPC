package com.liugeng.rpcframework.rpcclient.network.netty;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.rpcframework.rpcclient.network.NetworkConnector;
import com.liugeng.rpcframework.rpcclient.network.RpcFutureResponse;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import io.netty.channel.Channel;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:34
 */
public class NettyConnector implements NetworkConnector {
	
	private static final Logger log = LoggerFactory.getLogger(NettyConnector.class);
	
	private PooledNettyConnectionManager connectionManageStrategy;

	private ResponseHolder responseHolder;
	
	public NettyConnector(PooledNettyConnectionManager connectionManageStrategy, ResponseHolder responseHolder) {
		this.connectionManageStrategy = connectionManageStrategy;
		this.responseHolder = responseHolder;
		this.connectionManageStrategy.init();
	}
	
	@Override
	public RpcFutureResponse asyncSend(String address, RpcRequestPacket request) {
		RpcFutureResponse futureResponse = new RpcFutureResponse(request.getRequestId(), responseHolder);
		try {
			Channel channel = connectionManageStrategy.getConnection(address);
			channel.writeAndFlush(request).sync().addListener(new GenericFutureListener<Future<? super Void>>() {
				@Override
				public void operationComplete(Future<? super Void> future) throws Exception {
					System.out.println("是否发送成功：" + future.isSuccess());
				}
			});
			connectionManageStrategy.release(channel, address);
		} catch (Exception e) {
			futureResponse.setDone(true);
			futureResponse.setSuccess(false);
			futureResponse.setError(e);
		}
		return futureResponse;
	}
	
	@Override
	public void destroy() {
		connectionManageStrategy.destroy();
	}
	
	public static final String POOL_SIZE = "poolSize"; 
}
