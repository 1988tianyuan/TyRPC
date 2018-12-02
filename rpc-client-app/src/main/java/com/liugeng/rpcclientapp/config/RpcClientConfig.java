package com.liugeng.rpcclientapp.config;

import com.liugeng.rpcframework.registry.ServiceDiscovery;
import com.liugeng.rpcframework.rpcclient.RpcProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcClientConfig {

    @Value("${rpc.zkAddress}")
    private String zkAddress;

    @Value("${rpc.serviceName}")
    private String serviceName;

    @Bean
    public ServiceDiscovery serviceDiscovery() {
        return new ServiceDiscovery(zkAddress);
    }

    @Bean
    public RpcProxy rpcProxy(ServiceDiscovery serviceDiscovery) {
        return new RpcProxy(serviceName, serviceDiscovery);
    }

}
