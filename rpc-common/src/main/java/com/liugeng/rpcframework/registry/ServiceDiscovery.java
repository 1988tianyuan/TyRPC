package com.liugeng.rpcframework.registry;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.exception.RpcFrameworkException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;

public class ServiceDiscovery {

    private final CuratorFramework zkClient;

    public ServiceDiscovery(String zkAddress) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.zkClient =  CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        this.zkClient.start();
    }

    public List<String> discovery(String serviceName) {
        String servicePath = "/" + serviceName;
        try {
            Preconditions.checkNotNull(zkClient.checkExists().forPath(servicePath), "your request service doesn't exist !");
            return zkClient.getChildren().forPath(servicePath);
        } catch (Exception e) {
            throw new RpcFrameworkException("exception during discovery service: " + serviceName, e);
        }
    }





}
