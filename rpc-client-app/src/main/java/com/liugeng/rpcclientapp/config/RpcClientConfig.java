package com.liugeng.rpcclientapp.config;

import com.liugeng.rpcframework.registry.ServiceDiscovery;
import com.liugeng.rpcframework.rpcclient.proxy.ProxyFactory;
import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.service.ExampleService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcClientConfig {

    @Value("${rpc.zkAddress}")
    private String zkAddress;

    @Value("${rpc.serviceName}")
    private String serviceName;
    
    @Bean(destroyMethod = "finish")
    public ServiceDiscovery serviceDiscovery() {
        return new ServiceDiscovery(zkAddress);
    }

    @Bean
    public ExampleService exampleService(ServiceDiscovery discovery) {
        RpcProxy<ExampleService> exampleServiceProxy = ProxyFactory.newProxy(serviceName, discovery, ExampleService.class);
        return exampleServiceProxy.proxyInstance();
    }
}
