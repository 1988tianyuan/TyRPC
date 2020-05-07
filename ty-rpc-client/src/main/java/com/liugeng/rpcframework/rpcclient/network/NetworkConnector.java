package com.liugeng.rpcframework.rpcclient.network;

import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:34
 */
public interface NetworkConnector {
	
	void destroy();
	
	RpcFutureResponse asyncSend(String address, RpcRequestPacket request);
	
	RpcResponsePacket send(String address, RpcRequestPacket request);
}
