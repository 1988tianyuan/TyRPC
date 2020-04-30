package com.liugeng.rpcframework.rpcclient.network.connection;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 15:06
 */
public interface ConnectionManager<T> {
	
	Connection<T> getConnection(String address);
}
