package com.liugeng.rpcframework.utils;

import java.net.InetSocketAddress;

import com.google.common.base.Preconditions;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 15:56
 */
public class NetworkUtil {
	
	public static InetSocketAddress newSocketAddress(String address) {
		Preconditions.checkNotNull(address, "address should not be null!");
		String host = address.split(":")[0];
		int port = Integer.parseInt(address.split(":")[1]);
		return InetSocketAddress.createUnresolved(host, port);
	}
}
