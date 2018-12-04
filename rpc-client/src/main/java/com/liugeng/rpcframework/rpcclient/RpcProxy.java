package com.liugeng.rpcframework.rpcclient;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.registry.ServiceDiscovery;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;

public class RpcProxy {

    private static Logger logger = LoggerFactory.getLogger(RpcProxy.class);
    private final String rpcServerName;
    private final ServiceDiscovery serviceDiscovery;

    public RpcProxy(String rpcServerName, ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcServerName = rpcServerName;
    }

    @SuppressWarnings("unchecked")
    public <T> T createService(Class<T> serviceClazz) {
        Preconditions.checkNotNull(rpcServerName, "rpcServerName should not be null !");
        Preconditions.checkNotNull(serviceDiscovery, "serviceDiscovery should not be null !");
        RpcClient client = new RpcClient(rpcServerName, serviceDiscovery);
        return (T) Proxy.newProxyInstance(serviceClazz.getClassLoader(), new Class[]{serviceClazz}, new ProxyInvocationHandler(client));
    }

}
