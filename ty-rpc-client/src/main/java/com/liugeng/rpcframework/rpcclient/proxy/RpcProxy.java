package com.liugeng.rpcframework.rpcclient.proxy;

import java.lang.reflect.Proxy;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.rpcclient.client.FixAddressClient;
import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcclient.client.ServiceDiscoverClient;
import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

public class RpcProxy<T> {

    private final T proxyInstance;

    @SuppressWarnings("unchecked")
    RpcProxy(String serviceName, ServiceDiscovery serviceDiscovery, Class<T> proxyType, 
        LoadBalancerType lbType, SerializerType serializerType) {
        Preconditions.checkNotNull(serviceName, "rpcServerName should not be null !");
        Preconditions.checkNotNull(serviceDiscovery, "serviceDiscovery should not be null !");
        RpcClient client = new ServiceDiscoverClient(serviceName, serviceDiscovery, lbType, serializerType);
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(),
                new Class[]{proxyType}, new ProxyInvocationHandler(client));
    }
    
    @SuppressWarnings("unchecked")
    RpcProxy(String serviceAddress, Class<T> proxyType, SerializerType serializerType) {
        Preconditions.checkNotNull(serviceAddress, "serviceAddress should not be null !");
        RpcClient client = new FixAddressClient(serviceAddress, serializerType);
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(),
            new Class[]{proxyType}, new ProxyInvocationHandler(client));
    }
    
    public T proxyInstance() {
        return proxyInstance;
    }
}
