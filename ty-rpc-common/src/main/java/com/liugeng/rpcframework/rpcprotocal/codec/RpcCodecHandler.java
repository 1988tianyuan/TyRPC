package com.liugeng.rpcframework.rpcprotocal.codec;

import java.util.List;

import com.liugeng.rpcframework.rpcprotocal.model.RpcPacket;
import com.liugeng.rpcframework.rpcprotocal.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

public class RpcCodecHandler extends MessageToMessageCodec<ByteBuf, RpcPacket> {
    
    private Serializer serializer;
    
    public RpcCodecHandler(Serializer serializer) {
        this.serializer = serializer;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcPacket packet, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().ioBuffer();
        NettyPacketCodec.encode(buf, packet, serializer.getType());
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        RpcPacket packet = NettyPacketCodec.decode(byteBuf);
        out.add(packet);
    }
}
