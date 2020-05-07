package com.liugeng.rpcframework.rpcclient.proxy;

import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 13:41
 */
public class ProxyFactory {
	
	public static <T> RpcProxy<T> newProxy(String serviceName, ServiceDiscovery discovery, Class<T> proxyType,
		LoadBalancerType lbType, SerializerType serializerType) {
		return new RpcProxy<>(serviceName, discovery, proxyType, lbType, serializerType);
	}
	
	public static <T> RpcProxy<T> newProxy(String serviceAddress, Class<T> proxyType, SerializerType serializerType) {
		return new RpcProxy<>(serviceAddress, proxyType, serializerType);
	}
}
