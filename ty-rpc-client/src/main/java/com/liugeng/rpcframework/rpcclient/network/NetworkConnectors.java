package com.liugeng.rpcframework.rpcclient.network;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.liugeng.rpcframework.rpcclient.network.netty.NettyChannelInit;
import com.liugeng.rpcframework.rpcclient.network.netty.NettyConnector;
import com.liugeng.rpcframework.rpcclient.network.netty.PooledNettyConnectionManager;
import com.liugeng.rpcframework.rpcclient.network.netty.ResponseHolder;
import com.liugeng.rpcframework.rpcprotocal.serializer.Serializer;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:57
 */
public class NetworkConnectors {
	
	public static NetworkConnector newNettyConnector(Map<String, Object> connectConfig, Serializer serializer) {
		ResponseHolder responseHolder = new ResponseHolder();
		int poolSize = MapUtils.getIntValue(connectConfig, NettyConnector.POOL_SIZE, 0);
		PooledNettyConnectionManager connectionManager = 
			new PooledNettyConnectionManager(new NettyChannelInit(responseHolder, serializer), poolSize);
		return new NettyConnector(connectionManager, responseHolder);
	}

}
