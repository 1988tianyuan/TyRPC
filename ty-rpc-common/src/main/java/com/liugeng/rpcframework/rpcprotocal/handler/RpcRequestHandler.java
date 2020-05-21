package com.liugeng.rpcframework.rpcprotocal.handler;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequestPacket> {

    private final Map<String, Object> serviceMap;
    private static Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    public RpcRequestHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestPacket requestPacket) throws Exception{
        Preconditions.checkNotNull(serviceMap, "serviceMap is empty !");
        RpcResponsePacket responsePacket = new RpcResponsePacket();
        responsePacket.setRequestId(requestPacket.getRequestId());
        String serviceName = requestPacket.getClassName();
        String methodName = requestPacket.getMethodName();
        Class<?>[] paramsTypes = requestPacket.getParamTypes();
        Object[] params = requestPacket.getParams();
        Object serviceBean = serviceMap.get(serviceName);
        try {
            Preconditions.checkNotNull(serviceBean, "Cant's find the service:{} !", serviceName);
            logger.info("service: {} is being called...", serviceName);
            Method method = serviceBean.getClass().getMethod(methodName, paramsTypes);
            Object result = method.invoke(serviceBean, params);
            responsePacket.setResult(result);
        } catch (Exception e) {
            responsePacket.setError(e);
            logger.error("error during handle rpc request", e);
        }
        ctx.channel().writeAndFlush(responsePacket);
        logger.info("request: {} is handled.", requestPacket.getRequestId());
    }
}
