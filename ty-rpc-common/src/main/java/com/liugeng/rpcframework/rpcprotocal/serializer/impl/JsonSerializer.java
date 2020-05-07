package com.liugeng.rpcframework.rpcprotocal.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.liugeng.rpcframework.rpcprotocal.serializer.Serializer;
import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

public class JsonSerializer implements Serializer {

    @Override
    public byte getSerializerAlgorithm() {
        return SerializerType.JSON.getSequence();
    }

    @Override
    public byte[] serialize(Object object) {
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
    
    @Override
    public SerializerType getType() {
        return SerializerType.JSON;
    }
}
