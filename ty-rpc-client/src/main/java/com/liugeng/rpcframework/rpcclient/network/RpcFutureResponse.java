package com.liugeng.rpcframework.rpcclient.network;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:55
 */
public class RpcFutureResponse {

	private final String requestId;

	private volatile boolean done = false;

	private volatile boolean success = false;

	private volatile Throwable error;

	private ResponseHolder responseHolder;

	private Lock responseLock;

	private Condition responseCondition;
	
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
		}
		this.done = true;
		if (response != null) {
			this.success = true;
		}
		return response;
	}
	
	public String getRequestId() {
		return requestId;
	}
}
