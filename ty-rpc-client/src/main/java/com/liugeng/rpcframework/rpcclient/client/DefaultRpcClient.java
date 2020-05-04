package com.liugeng.rpcframework.rpcclient.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.liugeng.rpcframework.rpcclient.network.ConnectorFactory;
import com.liugeng.rpcframework.rpcclient.network.NettyConnector;
import com.liugeng.rpcframework.rpcclient.network.NetworkConnector;
import com.liugeng.rpcframework.rpcclient.network.RpcFutureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.registry.ServiceDiscovery;
import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultRpcClient implements RpcClient {
    private static Logger logger = LoggerFactory.getLogger(DefaultRpcClient.class);
    private String rpcServerName;
    private final ServiceDiscovery serviceDiscovery;
    private NetworkConnector connector;
    private long timeOut = 10;

    public DefaultRpcClient(String rpcServerName, ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcServerName = rpcServerName;
        Map<String, Object> configs = new HashMap<>();
        configs.put(NettyConnector.USE_POOL, true);
        this.connector = ConnectorFactory.newNettyConnector(configs);
    }
    
    @Override
    public RpcFutureResponse asyncSend(RpcRequestPacket requestPacket) {
        String hostAndPort = chooseAddress();
        return connector.asyncSend(hostAndPort, requestPacket);
    }

    private String chooseAddress() {
        List<String> addressList = serviceDiscovery.discovery(rpcServerName);
        Preconditions.checkArgument(!addressList.isEmpty(),
                "rpc server: {} is not available now !", rpcServerName);
        int size = addressList.size();
        int index = new Random().nextInt(size);
        return addressList.get(index);
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
}
