package com.liugeng.rpcframework.rpcclient.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcclient.network.NetworkConnector;
import com.liugeng.rpcframework.rpcclient.network.NetworkConnectors;
import com.liugeng.rpcframework.rpcclient.network.RpcFutureResponse;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import com.liugeng.rpcframework.rpcprotocal.serializer.Serializer;

public abstract class AbstractRpcClient implements RpcClient {
    private static Logger logger = LoggerFactory.getLogger(AbstractRpcClient.class);
    private NetworkConnector connector;
    private long timeOut = 10;
    private ExecutorService defaultCallbackExecutor = Executors.newSingleThreadExecutor();

    public AbstractRpcClient(Serializer serializer) {
        Map<String, Object> configs = new HashMap<>();
        this.connector = NetworkConnectors.newNettyConnector(configs, serializer);
    }
    
    @Override
    public void asyncSend(RpcRequestPacket requestPacket, RpcCallback callback, Executor executor) {
        if (executor == null) {
            executor = defaultCallbackExecutor;
        }
        try {
            String hostAndPort = chooseAddress();
            RpcFutureResponse futureResponse = connector.asyncSend(hostAndPort, requestPacket);
            if (!futureResponse.isSuccess() && futureResponse.getError() != null) {
                executor.execute(() -> callback.receiveResult(null, futureResponse.getError()));
                return;
            }
            logger.info("send rpc request to rpc server, request class: {}, request method: {}, request id: {}",
                requestPacket.getClassName(), requestPacket.getMethodName(), requestPacket.getRequestId());
            handleCallback(futureResponse, callback, executor);
        } catch (Throwable e) {
            executor.execute(() -> callback.receiveResult(null, e));
        }
    }
    
    private void handleCallback(RpcFutureResponse futureResponse, RpcCallback callback, Executor executor) {
        CompletableFuture
            .supplyAsync(() -> futureResponse.getResponse(timeOut, TimeUnit.SECONDS), executor)
            .whenComplete((response, throwable) -> {
                if (throwable != null) {
                    callback.receiveResult(null, throwable);
                } else if (response != null){
                    callback.receiveResult(response.getResult(), null);
                }
            });
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
            if (e instanceof RpcFrameworkException) {
                throw (RpcFrameworkException)e;
            }
            throw new RpcFrameworkException("exception during rpc request: " + requestPacket.getRequestId(), e);
        }
    }
    
    @Override
    public void stop() {
        connector.destroy();
        defaultCallbackExecutor.shutdown();
    }
    
    protected abstract String chooseAddress();
}
