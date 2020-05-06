package com.liugeng.rpcframework.rpcclient.client;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 17:19
 */
public class FixAddressClient extends AbstractRpcClient {
	
	private String address;
	
	public FixAddressClient(String address) {
		this.address = address;
	}
	
	@Override
	protected String chooseAddress() {
		return address;
	}
}
