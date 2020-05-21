package com.liugeng.rpcframework.rpcserver.server;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.registry.ServiceRegister;
import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.codec.Spliter;
import com.liugeng.rpcframework.rpcprotocal.handler.RpcRequestHandler;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcServer {

    protected static Logger logger = LoggerFactory.getLogger(RpcServer.class);

    protected final ConcurrentHashMap<String, Object> serviceMap = new ConcurrentHashMap<>();
    private String rpcAddress;
    private String serviceName;
    private ServiceRegister serviceRegister;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;

    public RpcServer(String serviceName, String rpcAddress, ServiceRegister serviceRegister) {
        this.rpcAddress = rpcAddress;
        this.serviceName = serviceName;
        this.serviceRegister = serviceRegister;
    }

    public void startServer() {
        Preconditions.checkNotNull(rpcAddress, "rpcAddress should not be null !");
        String[] hostAndPort = rpcAddress.split(":");
        String host = hostAndPort[0];
        int port = Integer.parseInt(hostAndPort[1]);

        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                       .channel(NioServerSocketChannel.class)
                       .handler(new ChannelInitializer<NioServerSocketChannel>() {
                           @Override
                           protected void initChannel(NioServerSocketChannel channel){
                               logger.info("服务端启动中...");
                           }
                       })
                       .childOption(ChannelOption.SO_KEEPALIVE, true)
                       .childOption(ChannelOption.TCP_NODELAY, true)
                       .option(ChannelOption.SO_BACKLOG, 1024)
                       .childHandler(new ChannelInitializer<NioSocketChannel>() {
                           @Override
                           protected void initChannel(NioSocketChannel channel) throws Exception {
                               ChannelPipeline pipeline = channel.pipeline();
                               pipeline.addLast(new Spliter());
                               pipeline.addLast(new RpcCodecHandler(SerializerType.JSON.getSerializer()));
                               pipeline.addLast(new RpcRequestHandler(serviceMap));
                           }
                       });

        serverBootstrap.bind(host, port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("RPC服务器启动成功, host：{}，端口号：{}", host, port);
                serviceRegistry();
            } else {
                logger.warn("RPC服务器启动失败！host：{}，端口号：{}", host, port);
            }
        });
    }

    private void serviceRegistry() throws InterruptedException {
        Preconditions.checkNotNull(serviceRegister, "serviceRegister should not be null !");
        serviceRegister.register(serviceName, rpcAddress);
        for(;;) {
            if (serviceRegister.isRegistered()) {
                logger.info("服务注册成功！地址：{}", rpcAddress);
                break;
            }
            Thread.sleep(1000);
        }
    }

    public void shutDown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        serviceRegister.deregister();
    }
}
