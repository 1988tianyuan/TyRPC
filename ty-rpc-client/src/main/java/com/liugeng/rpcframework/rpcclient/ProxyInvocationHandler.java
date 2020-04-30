package com.liugeng.rpcframework.rpcclient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import com.liugeng.rpcframework.rpcclient.client.DefaultRpcClient;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

public class ProxyInvocationHandler implements InvocationHandler {

    private final DefaultRpcClient rpcClient;

    public ProxyInvocationHandler(DefaultRpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequestPacket requestPacket = new RpcRequestPacket();
        requestPacket.setRequestId(UUID.randomUUID().toString()); 
        requestPacket.setClassName(method.getDeclaringClass().getName());
        requestPacket.setMethodName(method.getName());
        requestPacket.setParamTypes(method.getParameterTypes());
        requestPacket.setParams(args);
        RpcResponsePacket responsePacket = rpcClient.send(requestPacket, 5000);
        return responsePacket.getResult();
    }
}
