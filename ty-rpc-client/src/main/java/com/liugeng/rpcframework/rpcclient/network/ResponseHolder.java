package com.liugeng.rpcframework.rpcclient.network;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:35
 */
public class ResponseHolder {
	
	private volatile Map<String, RpcFutureResponse> responseMap = new ConcurrentHashMap<>();
	
	public boolean addFutureResponse() {
		
	}
}
