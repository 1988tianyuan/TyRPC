package com.liugeng.rpcframework.rpcserver.server;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.registry.ServiceRegister;
import com.liugeng.rpcframework.rpcprotocal.codec.RpcCodecHandler;
import com.liugeng.rpcframework.rpcprotocal.codec.Spliter;
import com.liugeng.rpcframework.rpcprotocal.handler.RpcRequestHandler;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;
import com.liugeng.rpcframework.rpcserver.annotation.RpcService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

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

    public void startServer() throws InterruptedException {
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

        serverBootstrap.bind(host, port).sync().addListener(future -> {
            if (future.isSuccess()) {
                logger.info("RPC服务器启动成功, host：{}，端口号：{}", host, port);
                if (serviceRegister != null) {
                    serviceRegistry();
                }
            } else {
                logger.warn("RPC服务器启动失败！host：{}，端口号：{}", host, port);
            }
        });
    }

    /* 循环注册当前服务，需要循环异步检查注册状态 */
    private void serviceRegistry() {
        workerGroup.submit(() -> {
            logger.info("开始注册服务，服务名：{}, 注册地址：{}", serviceName, rpcAddress);
            serviceRegister.register(serviceName, rpcAddress, true);
            while (!serviceRegister.isRegistered()) {
                logger.warn("服务注册失败，隔1秒再进行检查，注册地址：{}", rpcAddress);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
            logger.info("服务注册成功，注册地址：{}", rpcAddress);
        });
    }

    public void shutDown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        serviceRegister.deregister();
    }

    public void loadService(Class<?> serviceImpl) {
        RpcService annotation = serviceImpl.getAnnotation(RpcService.class);
        if (annotation != null) {
            Class<?> serviceType = annotation.value();
            String name = serviceType.getName();
            // 调用默认构造函数
            try {
                loadService(name, serviceImpl.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException |
                    InvocationTargetException | NoSuchMethodException e) {
                logger.error("Failed to load serviceImpl:{}", name, e);
            }
        }
    }

    public void loadService(String name, Object serviceInstance) {
        serviceMap.put(name, serviceInstance);
    }
}
