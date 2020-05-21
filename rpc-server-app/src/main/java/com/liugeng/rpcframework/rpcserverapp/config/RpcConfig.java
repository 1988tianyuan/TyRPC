package com.liugeng.rpcframework.rpcserverapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.liugeng.rpcframework.registry.ServiceRegister;
import com.liugeng.rpcframework.registry.impl.ZkServiceRegister;
import com.liugeng.rpcframework.rpcserver.server.RpcServer;
import com.liugeng.rpcframework.rpcserverapp.rpcserver.RpcServerInSpring;

@Configuration
public class RpcConfig {

    @Value("${rpc.rpcAddress}")
    private String rpcAddress;

    @Value("${rpc.zkAddress}")
    private String zkAddress;

    @Value("${rpc.serviceName}")
    private String serviceName;

    @Bean
    public RpcServer rpcServer(ServiceRegister serviceRegister) {
        return new RpcServerInSpring(serviceName, rpcAddress, serviceRegister);
    }

    @Bean
    public ServiceRegister serviceRegister() {
        return new ZkServiceRegister(zkAddress);
    }

}
