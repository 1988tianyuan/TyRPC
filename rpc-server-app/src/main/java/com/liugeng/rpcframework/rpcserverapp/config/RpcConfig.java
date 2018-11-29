package com.liugeng.rpcframework.rpcserverapp.config;

import com.liugeng.rpcframework.rpcserver.server.RpcServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RpcConfig {

    @Bean
    public RpcServer rpcServer() {
        RpcServer rpcServer = new RpcServer();
        return rpcServer;
    }

}
