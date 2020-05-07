package com.liugeng.rpcframework.rpcprotocal.serializer;

import java.util.Objects;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcprotocal.serializer.impl.JsonSerializer;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/7 14:30
 */
public enum SerializerType {
	
	JSON((byte)1, JsonSerializer.class);
	
	private byte sequence;
	
	private Class<? extends Serializer> serializerClazz;
	
	SerializerType(byte sequence, Class<? extends Serializer> serializerClazz) {
		this.sequence = sequence;
		this.serializerClazz = serializerClazz;
	}
	
	public byte getSequence() {
		return sequence;
	}
	
	public static Serializer instance(byte seq) {
		try {
			for (SerializerType type : values()) {
				if (Objects.equals(seq, type.getSequence())) {
					return type.serializerClazz.newInstance();
				}
			}
		} catch (Exception e) {
			throw new RpcFrameworkException(e);
		}
		return null;
	}
	
	public Serializer getSerializer() {
		try {
			return serializerClazz.newInstance();
		} catch (Exception e) {
			throw new RpcFrameworkException(e);	
		}
	}
}
