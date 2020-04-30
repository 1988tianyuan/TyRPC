package com.liugeng.rpcframework.rpcclient.proxy;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.registry.ServiceDiscovery;
import com.liugeng.rpcframework.rpcclient.ProxyInvocationHandler;
import com.liugeng.rpcframework.rpcclient.client.DefaultRpcClient;

public class RpcProxy<T> {

    private static Logger logger = LoggerFactory.getLogger(RpcProxy.class);
    private final String serviceName;
    private final ServiceDiscovery serviceDiscovery;
    private final T proxyInstance;

    @SuppressWarnings("unchecked")
    RpcProxy(String serviceName, ServiceDiscovery serviceDiscovery, Class<T> proxyType) {
        Preconditions.checkNotNull(serviceName, "rpcServerName should not be null !");
        Preconditions.checkNotNull(serviceDiscovery, "serviceDiscovery should not be null !");
        this.serviceDiscovery = serviceDiscovery;
        this.serviceName = serviceName;
        DefaultRpcClient client = new DefaultRpcClient(serviceName, serviceDiscovery);
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[]{proxyType}, new ProxyInvocationHandler(client));
    }
    
    public T proxyInstance() {
        return proxyInstance;
    }
}
