package com.liugeng.rpcframework.rpcclient.network;

import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:34
 */
public interface NetworkConnector {
	
	void destroy();
	
	RpcFutureResponse asyncSend(String address, RpcRequestPacket request);
}
