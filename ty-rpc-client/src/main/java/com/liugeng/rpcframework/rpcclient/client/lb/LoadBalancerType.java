package com.liugeng.rpcframework.rpcclient.client.lb;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 18:23
 */
public enum LoadBalancerType {
	
	RANDOM(new RandomStrategy());
	
	private LoadBalancerStrategy loadBalancerStrategy;
	
	LoadBalancerType(LoadBalancerStrategy loadBalancerStrategy) {
		this.loadBalancerStrategy = loadBalancerStrategy;
	}
	
	public LoadBalancerStrategy getLoadBalancerStrategy() {
		return loadBalancerStrategy;
	}
}
