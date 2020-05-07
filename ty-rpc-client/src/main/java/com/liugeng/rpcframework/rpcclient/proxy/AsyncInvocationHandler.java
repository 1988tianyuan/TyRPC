package com.liugeng.rpcframework.rpcclient.proxy;

import java.util.concurrent.Executor;

import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;

public class AsyncInvocationHandler extends RpcInvocationHandler {
    
    private RpcClient.RpcCallback callback;
    
    private Executor callbackExecutor;

    public AsyncInvocationHandler(RpcClient rpcClient, RpcClient.RpcCallback callback, Executor callbackExecutor) {
        super(rpcClient);
        this.callback = callback;
        this.callbackExecutor = callbackExecutor;
    }
    
    @Override
    protected Object doInvoke(RpcRequestPacket requestPacket) {
        rpcClient.asyncSend(requestPacket, callback, callbackExecutor);
        return null;
    }
}
