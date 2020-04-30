package com.liugeng.rpcframework.rpcprotocal.codec;

import com.alibaba.fastjson.JSON;
import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcprotocal.model.RpcPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import io.netty.buffer.ByteBuf;

public class RpcPacketCodec {

    public static final int MAGIC_NUMBER = 0x12345678;

    //将对象序列化到ByteBuf中
    public static ByteBuf encode(ByteBuf byteBuf, RpcPacket packet) {
        byte[] bytes = JSON.toJSONBytes(packet);
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

    public static RpcPacket decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);
        byteBuf.skipBytes(1);
        byte command = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Class<? extends RpcPacket> targetType = getType(command);
        return JSON.parseObject(data, targetType);
    }

    private static Class<? extends RpcPacket> getType(byte command) {
        switch (command) {
            case 1:
                return RpcRequestPacket.class;
            case 2:
                return RpcResponsePacket.class;
            default:
                throw new RpcFrameworkException("no class exists corresponding to the specific command !");
        }
    }



}
