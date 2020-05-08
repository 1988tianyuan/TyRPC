package com.liugeng.rpcclientapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;
import com.liugeng.rpcframework.rpcclient.proxy.ProxyFactory;
import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;
import com.liugeng.rpcframework.service.ExampleService;

@Configuration
public class RpcClientConfig {

    @Value("${rpc.zkAddress}")
    private String zkAddress;

    @Value("${rpc.serviceName}")
    private String serviceName;
    
    @Value("${rpc.serviceAddress}")
    private String address;
    
    @Value("${rpc.loadbalancer.type}")
    private LoadBalancerType loadBalancerType;
    
    @Bean(destroyMethod = "finish")
    public ServiceDiscovery serviceDiscovery() {
        return new ServiceDiscovery(zkAddress);
    }

    @Bean(destroyMethod = "finish")
    public RpcProxy<ExampleService> exampleService(ServiceDiscovery discovery) {
        return ProxyFactory.newSyncProxy(serviceName, discovery, ExampleService.class, loadBalancerType, SerializerType.JSON);
    }
    
    @Bean(destroyMethod = "finish")
    public RpcProxy<ExampleService> asyncExampleService() {
        return ProxyFactory.newAsyncProxy(address, ExampleService.class, SerializerType.JSON,
            (result, error) -> System.out.println("收到回复：" + result), null);
    }
    
//    @Bean
    public RpcProxy<ExampleService> exampleService() {
        return ProxyFactory.newSyncProxy(address, ExampleService.class, SerializerType.JSON);
    }
}
