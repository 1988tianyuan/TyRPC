package com.liugeng.rpcframework.rpcclient.network;

import java.util.concurrent.TimeUnit;

import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:55
 */
public abstract class RpcFutureResponse {
	
	protected volatile RpcResponsePacket response;
	
	protected final String requestId;
	
	protected volatile boolean done = false;
	
	protected volatile boolean success = false;
	
	protected volatile Throwable error;
	
	public void setResponse(RpcResponsePacket response) {
		this.response = response;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void setError(Throwable error) {
		this.error = error;
	}
	
	public RpcFutureResponse(String requestId) {
		this.requestId = requestId;
	}
	
	public abstract RpcResponsePacket getResponse(long time, TimeUnit timeUnit);
	
	public String getRequestId() {
		return requestId;
	}
}
