package com.liugeng.rpcframework.rpcclient.network.netty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.liugeng.rpcframework.rpcprotocal.model.RpcResponsePacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/4/30 16:35
 */
public class ResponseHolder {
	
	private final Map<String, ResponseWrapper> responseMap = new ConcurrentHashMap<>();

	public void addResponseLock(String requestId, Lock lock, Condition condition) {
		ResponseWrapper responseWrapper = new ResponseWrapper();
		responseWrapper.setResponseLock(lock);
		responseWrapper.setResponseCondition(condition);
		responseMap.put(requestId, responseWrapper);
	}
	
	public void addResponse(RpcResponsePacket response) {
		String requestId = response.getRequestId();
		ResponseWrapper responseWrapper;
		if ((responseWrapper = responseMap.get(requestId)) != null) {
			Lock lock = responseWrapper.getResponseLock();
			try {
				lock.lock();
				if (responseWrapper.getResponse() == null) {
					responseWrapper.setResponse(response);
				}
				responseWrapper.getResponseCondition().signalAll();
			} finally {
				lock.unlock();
			}
		}
	}

	public void removeResponse(String requestId) {
		responseMap.remove(requestId);
	}

	public RpcResponsePacket getResponse(String requestId) {
		ResponseWrapper wrapper = responseMap.get(requestId);
		if (wrapper != null) {
			return wrapper.getResponse();
		}
		return null;
	}

	public static class ResponseWrapper {
		private RpcResponsePacket response;

		private Lock responseLock;

		private Condition responseCondition;

		public RpcResponsePacket getResponse() {
			return response;
		}

		public void setResponse(RpcResponsePacket response) {
			this.response = response;
		}

		public Lock getResponseLock() {
			return responseLock;
		}

		public void setResponseLock(Lock responseLock) {
			this.responseLock = responseLock;
		}

		public Condition getResponseCondition() {
			return responseCondition;
		}

		public void setResponseCondition(Condition responseCondition) {
			this.responseCondition = responseCondition;
		}
	}
}
