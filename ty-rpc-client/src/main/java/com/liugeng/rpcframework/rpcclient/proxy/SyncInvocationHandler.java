package com.liugeng.rpcframework.rpcclient.proxy;

import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

public class SyncInvocationHandler extends RpcInvocationHandler {

    public SyncInvocationHandler(RpcClient rpcClient) {
        super(rpcClient);
    }
    
    @Override
    protected Object doInvoke(RpcRequestPacket requestPacket) throws Throwable {
        RpcResponsePacket response = rpcClient.send(requestPacket);
        if (response.getError() != null) {
            throw response.getError();
        }
        return response.getResult();
    }
}
