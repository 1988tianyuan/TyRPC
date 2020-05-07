package com.liugeng.rpcframework.rpcclient.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcclient.network.NetworkConnector;
import com.liugeng.rpcframework.rpcclient.network.NetworkConnectors;
import com.liugeng.rpcframework.rpcclient.network.RpcFutureResponse;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

public abstract class AbstractRpcClient implements RpcClient {
    private static Logger logger = LoggerFactory.getLogger(AbstractRpcClient.class);
    private NetworkConnector connector;
    private long timeOut = 10;

    public AbstractRpcClient() {
        Map<String, Object> configs = new HashMap<>();
        this.connector = NetworkConnectors.newNettyConnector(configs);
    }
    
    @Override
    public RpcFutureResponse asyncSend(RpcRequestPacket requestPacket) {
        String hostAndPort = chooseAddress();
        return connector.asyncSend(hostAndPort, requestPacket);
    }
    
    @Override
    public RpcResponsePacket send(RpcRequestPacket requestPacket) {
        try {
            String hostAndPort = chooseAddress();
            RpcFutureResponse futureResponse = connector.asyncSend(hostAndPort, requestPacket);
            if (!futureResponse.isSuccess() && futureResponse.getError() != null) {
                throw futureResponse.getError(); 
            }
            logger.info("send rpc request to rpc server, request class: {}, request method: {}, request id: {}",
                    requestPacket.getClassName(), requestPacket.getMethodName(), requestPacket.getRequestId());
            return futureResponse.getResponse(timeOut, TimeUnit.SECONDS);
        } catch (Throwable e) {
            throw new RpcFrameworkException("exception during rpc request: " + requestPacket.getRequestId(), e);
        }
    }
    
    @Override
    public void stop() {
        connector.destroy();
    }
    
    protected abstract String chooseAddress();
}
