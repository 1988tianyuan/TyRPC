package com.liugeng.rpcframework.rpcclient.client.lb;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.liugeng.rpcframework.rpcclient.client.RpcInstance;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 18:04
 */
public class RandomStrategy implements LoadBalancerStrategy {
	
	@Override
	public RpcInstance selectInstance(List<RpcInstance> instanceList) {
		int size = instanceList.size();
		int index = ThreadLocalRandom.current().nextInt(size);
		return instanceList.get(index);
	}
}
