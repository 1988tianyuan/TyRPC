package com.liugeng.rpcframework.rpcprotocal.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class Spliter extends LengthFieldBasedFrameDecoder {

    public static final Integer LENGTH_FIELD_OFFSET = 7;
    public static final Integer LENGTH_FIELD_SIZE = 4;

    public Spliter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_SIZE);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        int magicNum = in.getInt(in.readerIndex());
        //通过魔数判断是否是合法的请求
        if(magicNum != NettyPacketCodec.MAGIC_NUMBER) {
            ctx.channel().close();
            return null;
        }
        return super.decode(ctx, in);
    }
}
