package com.liugeng.rpcframework.rpcprotocal.codec;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcprotocal.model.RpcPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;
import com.liugeng.rpcframework.rpcprotocal.serializer.Serializer;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;
import io.netty.buffer.ByteBuf;

public class NettyPacketCodec {

    public static final int MAGIC_NUMBER = 0x12345678;

    //将对象序列化到ByteBuf中
    public static void encode(ByteBuf byteBuf, RpcPacket packet, SerializerType serializerType) {
        byte[] bytes = serializerType.getSerializer().serialize(packet);
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(serializerType.getSequence());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public static RpcPacket decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);
        byteBuf.skipBytes(1);
        byte serializerSeq = byteBuf.readByte();
        byte command = byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Class<? extends RpcPacket> targetType = getType(command);
        Serializer serializer = SerializerType.instance(serializerSeq);
        if (serializer == null) {
            serializer = SerializerType.JSON.getSerializer();
        }
        return serializer.deserialize(targetType, data);
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
