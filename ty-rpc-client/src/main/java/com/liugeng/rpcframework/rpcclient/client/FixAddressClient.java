package com.liugeng.rpcframework.rpcclient.client;

import com.liugeng.rpcframework.rpcprotocal.serializer.SerializerType;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 17:19
 */
public class FixAddressClient extends AbstractRpcClient {
	
	private String address;
	
	public FixAddressClient(String address, SerializerType serializerType) {
		super(serializerType.getSerializer());
		this.address = address;
	}
	
	@Override
	protected String chooseAddress() {
		return address;
	}
}
