package com.liugeng.rpcframework.rpcprotocal.codec;

import com.liugeng.rpcframework.rpcprotocal.model.RpcPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

public class RpcCodecHandler extends MessageToMessageCodec<ByteBuf, RpcPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcPacket packet, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().ioBuffer();
        RpcPacketCodec.encode(buf, packet);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        RpcPacket packet = RpcPacketCodec.decode(byteBuf);
        out.add(packet);
    }
}
