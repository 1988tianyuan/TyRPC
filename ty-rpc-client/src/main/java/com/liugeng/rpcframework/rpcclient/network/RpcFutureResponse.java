package com.liugeng.rpcframework.rpcclient.network;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcclient.network.netty.ResponseHolder;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:55
 */
public class RpcFutureResponse {
	
	protected final String requestId;
	
	protected volatile boolean done = false;
	
	protected volatile boolean success = false;
	
	protected volatile Throwable error;
	
	private final ResponseHolder responseHolder;
	
	private final Lock responseLock;
	
	private final Condition responseCondition;
	
	public RpcFutureResponse(String requestId, ResponseHolder responseHolder) {
		this.requestId = requestId;
		this.responseHolder = responseHolder;
		this.responseLock = new ReentrantLock();
		this.responseCondition = responseLock.newCondition();
		this.responseHolder.addResponseLock(requestId, responseLock, responseCondition);
	}
	
	public RpcResponsePacket getResponse(long time, TimeUnit timeUnit) {
		RpcResponsePacket response = null;
		try {
			responseLock.lock();
			if ((response = responseHolder.getResponse(requestId)) == null) {
				responseCondition.await(time, timeUnit);
				response = responseHolder.getResponse(requestId);
			}
		} catch (Exception e) {
			// nothing
		} finally {
			responseHolder.removeResponse(requestId);
			responseLock.unlock();
			done = true;
		}
		if (response != null) {
			success = true;
		} else {
			throw new RpcFrameworkException("Request: [" + requestId + "] failed after timeout:" +
				timeUnit.toSeconds(time) + "seconds.");
		}
		return response;
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
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public Throwable getError() {
		return error;
	}
	
	public String getRequestId() {
		return requestId;
	}
}
