package com.liugeng.rpcframework.rpcclient.proxy;

import java.util.concurrent.Executor;

import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 13:41
 */
public class ProxyFactory {
	
	public static <T> RpcProxy<T> newSyncProxy(String serviceName, ServiceDiscovery discovery, Class<T> proxyType,
		LoadBalancerType lbType, SerializerType serializerType) {
		RpcProxy<T> proxy = new RpcProxy<>(proxyType, serializerType);
		proxy.initDiscoverClient(serviceName, discovery, lbType);
		proxy.initSyncInstance();
		return proxy;
	}
	
	public static <T> RpcProxy<T> newSyncProxy(String serviceAddress, Class<T> proxyType, SerializerType serializerType) {
		RpcProxy<T> proxy = new RpcProxy<>(proxyType, serializerType);
		proxy.initFixAddrClient(serviceAddress);
		proxy.initSyncInstance();
		return proxy;
	}
	
	public static <T> RpcProxy<T> newAsyncProxy(String serviceName, ServiceDiscovery discovery, Class<T> proxyType,
		LoadBalancerType lbType, SerializerType serializerType, RpcClient.RpcCallback callback, Executor callbackExecutor) {
		RpcProxy<T> proxy = new RpcProxy<>(proxyType, serializerType);
		proxy.initDiscoverClient(serviceName, discovery, lbType);
		proxy.initAsyncInstance(callback, callbackExecutor);
		return proxy;
	}
	
	public static <T> RpcProxy<T> newAsyncProxy(String serviceAddress, Class<T> proxyType, SerializerType serializerType, 
		RpcClient.RpcCallback callback, Executor callbackExecutor) {
		RpcProxy<T> proxy = new RpcProxy<>(proxyType, serializerType);
		proxy.initFixAddrClient(serviceAddress);
		proxy.initAsyncInstance(callback, callbackExecutor);
		return proxy;
	}
}
