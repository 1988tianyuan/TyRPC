package com.liugeng.rpcframework.example;

import com.liugeng.rpcframework.rpcclient.proxy.ProxyFactory;
import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

public class TestMain {

    public static void main(String[] args) {
        String serviceAddr = "localhost:8000";
        RpcProxy<ExampleService> proxy =
                ProxyFactory.newSyncProxy(serviceAddr, ExampleService.class, SerializerType.JSON);
        ExampleService exampleService = proxy.proxyInstance();
        String result = exampleService.doSomething("name", "liugeng");
        System.out.println("结果是：" + result);
    }
}
