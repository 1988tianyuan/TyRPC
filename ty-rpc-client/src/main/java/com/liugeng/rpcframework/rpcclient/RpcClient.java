package com.liugeng.rpcframework.rpcclient;

import java.util.List;
import java.util.Random;

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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient {
    private static Logger logger = LoggerFactory.getLogger(RpcClient.class);
    private static final int MAX_RETRY = 5;
    private String rpcServerName;
    private final ServiceDiscovery serviceDiscovery;
    private RpcResponsePacket responsePacket;
    private NioEventLoopGroup workerGroup;

    public RpcClient(String rpcServerName, ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcServerName = rpcServerName;
    }

    public RpcResponsePacket send(RpcRequestPacket requestPacket, long timeOut) {
        long cancelTime = System.currentTimeMillis() + timeOut;
        Channel channel = startConnect();
        if (channel != null) {
            logger.info("rpc connection is built successfully !");
        } else {
            throw new RpcFrameworkException("build rpc connection failed, finish this request !");
        }
        try {
            channel.writeAndFlush(requestPacket).sync();
            logger.info("send rpc request to rpc server, request class: {}, request method: {}, request id: {}",
                    requestPacket.getClassName(), requestPacket.getMethodName(), requestPacket.getRequestId());
            for (;;) {
                if (this.responsePacket != null) {
                    logger.info("received response, id: {}", responsePacket.getRequestId());
                    channel.close();
                    if (!requestPacket.getRequestId().equals(responsePacket.getRequestId())) {
                        throw new RpcFrameworkException("response id is not the same with request, ignore this response !");
                    }
                    if (responsePacket.isError()) {
                        throw responsePacket.getError();
                    }
                    return responsePacket;
                } else if (System.currentTimeMillis() > cancelTime) {
                    throw new RpcFrameworkException("rpc request timeout, cancel this connection !");
                }
            }
        } catch (Throwable e) {
            throw new RpcFrameworkException("exception during rpc request: " + requestPacket.getRequestId(), e);
        } finally {
            workerGroup.shutdownGracefully();
            channel.close().addListener(future -> logger.info("rpc connection is closed."));
        }
    }

    private Channel startConnect() {
        List<String> rpcServerAddressList = serviceDiscovery.discovery(rpcServerName);
        Preconditions.checkArgument(!rpcServerAddressList.isEmpty(), "rpc server: {} is not available now !", rpcServerName);
        String hostAndPort = chooseAddress(rpcServerAddressList);
        logger.info("rpc server address: " + hostAndPort);
        this.workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                 .group(workerGroup)
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                 .handler(new ChannelInitializer<NioSocketChannel>() {
                     @Override
                     protected void initChannel(NioSocketChannel ch) throws Exception {
                         ch.pipeline().addLast(new RpcCodecHandler())
                                      .addLast(new SimpleChannelInboundHandler<RpcResponsePacket>() {
                                          @Override
                                          protected void channelRead0(ChannelHandlerContext ctx, RpcResponsePacket packet) throws Exception {
                                              responsePacket = packet;
                                          }
                                      });
                     }
                 });
        return connect(bootstrap, hostAndPort, MAX_RETRY);
    }

    private String chooseAddress(List<String> addressList) {
        int size = addressList.size();
        int index = new Random().nextInt(size);
        return addressList.get(index);
    }

    private Channel connect(Bootstrap bootstrap, String hostAndPort, int retry){
        String host = hostAndPort.split(":")[0];
        int port = Integer.parseInt(hostAndPort.split(":")[1]);
        try {
            return bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            throw new RpcFrameworkException("exception during connect to rpc server", e);
        }
    }
}
