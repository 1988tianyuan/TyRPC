package com.liugeng.rpcframework.example;

import com.liugeng.rpcframework.rpcserver.server.RpcServer;

public class TestMain {

    public static void main(String[] args) throws InterruptedException {
        String serviceName = "testService";
        RpcServer rpcServer = new RpcServer(serviceName, "0.0.0.0:8000", null);
        rpcServer.loadService(ExampleServiceImpl.class);
        rpcServer.startServer();
    }
}
