package com.liugeng.rpcframework.rpcclient.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:35
 */
public class ResponseHolder {
	
	private volatile Map<String, RpcFutureResponse> responseMap = new ConcurrentHashMap<>();
	
	public void addFutureResponse(RpcFutureResponse futureResponse) {
		responseMap.put(futureResponse.getRequestId(), futureResponse);
	}
	
	public boolean addResponse(RpcResponsePacket response) {
		String requestId = response.getRequestId();
		RpcFutureResponse futureResponse = responseMap.get(requestId);
		if (futureResponse != null) {
			futureResponse.setResponse(response);
			return true;
		} else {
			return false;
		}
	}
}
