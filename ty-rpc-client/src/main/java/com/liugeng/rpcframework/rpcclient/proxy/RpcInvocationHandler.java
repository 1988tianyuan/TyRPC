package com.liugeng.rpcframework.rpcclient.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import com.liugeng.rpcframework.rpcclient.client.RpcClient;
import com.liugeng.rpcframework.rpcprotocal.model.RpcRequestPacket;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/7 18:08
 */
public abstract class RpcInvocationHandler implements InvocationHandler {
	
	protected final RpcClient rpcClient;
	
	public RpcInvocationHandler(RpcClient rpcClient) {
		this.rpcClient = rpcClient;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		RpcRequestPacket requestPacket = new RpcRequestPacket();
		requestPacket.setRequestId(UUID.randomUUID().toString());
		requestPacket.setClassName(method.getDeclaringClass().getName());
		requestPacket.setMethodName(method.getName());
		requestPacket.setParamTypes(method.getParameterTypes());
		requestPacket.setParams(args);
		return doInvoke(requestPacket);
	}
	
	protected abstract Object doInvoke(RpcRequestPacket requestPacket) throws Throwable;
}
