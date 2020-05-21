package com.liugeng.rpcframework.registry;

/**
 * @author Liu Geng liu.geng@navercorp.com
 * @date 2020/5/21 21:02
 */
public interface ServiceRegister {
	
	void register(String serviceName, String registerAddress);
	
	void deregister();
	
	boolean isRegistered();
}
