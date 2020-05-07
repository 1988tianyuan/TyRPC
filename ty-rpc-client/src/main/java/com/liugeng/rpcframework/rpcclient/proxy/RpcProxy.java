package com.liugeng.rpcframework.rpcclient.proxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.rpcclient.client.FixAddressClient;
import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcclient.client.ServiceDiscoverClient;
import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

public class RpcProxy<T> {

    private T proxyInstance;
    
    private final Class<T> proxyType;
    
    private RpcClient client;
    
    private SerializerType serializerType;
    
    RpcProxy(Class<T> proxyType, SerializerType serializerType) {
        this.proxyType = proxyType;
        this.serializerType = serializerType;
    }
    
    public void initDiscoverClient(String serviceName, ServiceDiscovery serviceDiscovery, LoadBalancerType lbType) {
        Preconditions.checkNotNull(serviceName, "rpcServerName should not be null !");
        Preconditions.checkNotNull(serviceDiscovery, "serviceDiscovery should not be null !");
        this.client = new ServiceDiscoverClient(serviceName, serviceDiscovery, lbType, serializerType);
    }
    
    public void initFixAddrClient(String serviceAddress) {
        Preconditions.checkNotNull(serviceAddress, "serviceAddress should not be null !");
        this.client = new FixAddressClient(serviceAddress, serializerType);
    }
    
    @SuppressWarnings("unchecked")
    public void initSyncInstance() {
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(),
            new Class[]{proxyType}, new SyncInvocationHandler(client));
    }
    
    @SuppressWarnings("unchecked")
    public void initAsyncInstance(RpcClient.RpcCallback callback, Executor callbackExecutor) {
        this.proxyInstance = (T) Proxy.newProxyInstance(proxyType.getClassLoader(),
            new Class[]{proxyType}, new AsyncInvocationHandler(client, callback, callbackExecutor));
    }
    
    public T proxyInstance() {
        return proxyInstance;
    }
    
    public void finish() {
        client.stop();
    }
}
