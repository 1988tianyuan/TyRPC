package com.liugeng.rpcframework.rpcclient.client;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/6 18:01
 */
public class RpcInstance {
	
	private String address;
	
	public RpcInstance(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
}
