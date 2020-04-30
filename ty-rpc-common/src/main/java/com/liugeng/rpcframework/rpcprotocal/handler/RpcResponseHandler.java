package com.liugeng.rpcframework.rpcprotocal.handler;

import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponsePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponsePacket responsePacket) throws Exception {

    }
}
