package com.liugeng.rpcframework.rpcclient.network.connection;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 15:09
 */
public abstract class Connection<T> {
	
	public Connection(String connectionId, T wrappedConnection) {
		this.connectionId = connectionId;
		this.wrappedConnection = wrappedConnection;
	}
	
	private String connectionId;
	
	private T wrappedConnection;
	
	public T getWrappedConnection() {
		return wrappedConnection;
	}
	
	public String getConnectionId() {
		return connectionId;
	}
	
	public abstract void destroy() throws Exception;
}
