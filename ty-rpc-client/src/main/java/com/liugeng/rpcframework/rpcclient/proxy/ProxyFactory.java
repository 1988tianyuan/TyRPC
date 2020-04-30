package com.liugeng.rpcframework.rpcclient.proxy;

import com.liugeng.rpcframework.registry.ServiceDiscovery;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 13:41
 */
public class ProxyFactory {
	
	public static <T> RpcProxy<T> newProxy(String serviceName, ServiceDiscovery discovery, Class<T> proxyType) {
		return new RpcProxy<>(serviceName, discovery, proxyType);
	}
}
