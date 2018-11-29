package com.liugeng.rpcframework.rpcserver.server;

import java.util.concurrent.ConcurrentHashMap;

public class RpcServer {

    private final ConcurrentHashMap<String, Class<?>> serviceMap = new ConcurrentHashMap<>();
    private String rpcAddress;




}
