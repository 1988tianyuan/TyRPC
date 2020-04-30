package com.liugeng.rpcframework.rpcclient.client;

import java.util.concurrent.CompletableFuture;

import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 14:04
 */
public interface RpcClient {
	
	RpcResponsePacket send(RpcRequestPacket requestPacket);
	
	CompletableFuture<RpcResponsePacket> asyncSend(RpcRequestPacket requestPacket);
}
