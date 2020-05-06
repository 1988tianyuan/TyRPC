package com.liugeng.rpcframework.rpcclient.proxy;

import java.lang.reflect.Proxy;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.rpcclient.client.FixAddressClient;
import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcclient.client.ServiceDiscoverClient;
import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;

public class RpcProxy<T> {

    private final T proxyInstance;

    @SuppressWarnings("unchecked")
    RpcProxy(String serviceName, ServiceDiscovery serviceDiscovery, Class<T> proxyType, LoadBalancerType lbType) {
        Preconditions.checkNotNull(serviceName, "rpcServerName should not be null !");
        Preconditions.checkNotNull(serviceDiscovery, "serviceDiscovery should not be null !");
        RpcClient client = new ServiceDiscoverClient(serviceName, serviceDiscovery, lbType);
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(),
                new Class[]{proxyType}, new ProxyInvocationHandler(client));
    }
    
    @SuppressWarnings("unchecked")
    RpcProxy(String serviceAddress, Class<T> proxyType) {
        Preconditions.checkNotNull(serviceAddress, "serviceAddress should not be null !");
        RpcClient client = new FixAddressClient(serviceAddress);
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(),
            new Class[]{proxyType}, new ProxyInvocationHandler(client));
    }
    
    public T proxyInstance() {
        return proxyInstance;
    }
}
