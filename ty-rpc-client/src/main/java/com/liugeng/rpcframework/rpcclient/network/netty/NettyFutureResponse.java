package com.liugeng.rpcframework.rpcclient.network.netty;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.liugeng.rpcframework.rpcclient.network.RpcFutureResponse;
import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:55
 */
public class NettyFutureResponse extends RpcFutureResponse {

	private ResponseHolder responseHolder;

	private Lock responseLock;

	private Condition responseCondition;

	public NettyFutureResponse(String requestId, ResponseHolder responseHolder) {
		super(requestId);
		this.responseHolder = responseHolder;
		this.responseLock = new ReentrantLock();
		this.responseCondition = responseLock.newCondition();
		this.responseHolder.addResponseLock(requestId, responseLock, responseCondition);
	}

	@Override
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
}
