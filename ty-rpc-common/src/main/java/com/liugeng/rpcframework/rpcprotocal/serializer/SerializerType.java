package com.liugeng.rpcframework.rpcprotocal.serializer;

import java.util.Objects;

import com.liugeng.rpcframework.rpcprotocal.serializer.impl.JsonSerializer;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/7 14:30
 */
public enum SerializerType {
	
	JSON((byte)1, new JsonSerializer());
	
	private byte sequence;
	
	private Serializer serializer;
	
	SerializerType(byte sequence, Serializer serializer) {
		this.sequence = sequence;
		this.serializer = serializer;
	}
	
	public byte getSequence() {
		return sequence;
	}
	
	public static Serializer instance(byte seq) {
		for (SerializerType type : values()) {
			if (Objects.equals(seq, type.getSequence())) {
				return type.serializer;
			}
		}
		return null;
	}
	
	public Serializer getSerializer() {
		return serializer;
	}
}
