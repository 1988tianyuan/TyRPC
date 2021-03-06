package com.liugeng.rpcframework.registry.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.registry.ServiceRegister;

public class ZkServiceRegister implements ServiceRegister {

    private final CuratorFramework zkClient;
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceRegister.class);
    private volatile boolean registered = false;
    private String servicePath;

    public ZkServiceRegister(String zkAddress) {
        this.zkClient = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        this.zkClient.start();
    }
    
    @Override
    public void register(String serviceName, String registerAddress, boolean retry) {
        Preconditions.checkNotNull(serviceName, "serviceName should not be null !");
        registerService(serviceName, registerAddress);
        this.zkClient.getConnectionStateListenable().addListener((curatorFramework, connectionState) -> {
            boolean isConnect = connectionState.isConnected();
            if (retry && isConnect && !registered) {
                // retry register to zk
                registerService(serviceName, registerAddress);
            } else {
                registered = false;
            }
        });
    }

    private void registerService(String serviceName, String serviceAddress) {
        Preconditions.checkNotNull(zkClient, "zkClient is not be initialized !");
        String serviceRootPath = "/" + serviceName;
        servicePath = serviceRootPath + "/" + serviceAddress;
        try {
            if (zkClient.checkExists().forPath(serviceRootPath) == null) {
                zkClient.create().forPath(serviceRootPath, serviceName.getBytes());
            }
            if (zkClient.checkExists().forPath(servicePath) == null) {
                zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, serviceAddress.getBytes());
            }
            logger.info("success to register on service: {}, zk path is: {}", serviceName, servicePath);
            this.registered = true;
        } catch (Exception e) {
            logger.error("fail to register on service: {}, zk path is: {}", serviceName, servicePath, e);
        }
    }
    
    @Override
    public void deregister() {
        try {
            if (registered) {
                zkClient.delete().forPath(servicePath);
                registered = false;
            }
            zkClient.close();
        } catch (Exception e) {
            logger.error("fail to deregister zk path: {}", servicePath, e);
        }
    }
    
    @Override
    public boolean isRegistered() {
        return registered;
    }
}
