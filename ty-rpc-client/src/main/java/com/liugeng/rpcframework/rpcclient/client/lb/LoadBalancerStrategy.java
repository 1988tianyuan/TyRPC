package com.liugeng.rpcframework.rpcclient.client.lb;

import java.util.List;

import com.liugeng.rpcframework.rpcclient.client.RpcInstance;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 17:55
 */
public interface LoadBalancerStrategy {
	
	RpcInstance selectInstance(List<RpcInstance> instanceList);
}
