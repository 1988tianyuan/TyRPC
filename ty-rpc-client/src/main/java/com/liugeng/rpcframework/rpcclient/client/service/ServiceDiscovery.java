package com.liugeng.rpcframework.rpcclient.client.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.exception.RpcFrameworkException;
import com.liugeng.rpcframework.rpcclient.client.RpcInstance;

public class ServiceDiscovery {

    private final CuratorFramework zkClient;

    public ServiceDiscovery(String zkAddress) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.zkClient =  CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        this.zkClient.start();
    }

    public List<RpcInstance> discovery(String serviceName) {
        String servicePath = "/" + serviceName;
        try {
            Preconditions.checkNotNull(zkClient.checkExists().forPath(servicePath), "your request service doesn't exist !");
            List<String> addressList = zkClient.getChildren().forPath(servicePath);
            Preconditions.checkArgument(!addressList.isEmpty(), "rpc server: {} is not available now !", serviceName);
            return addressList.stream().map(RpcInstance::new).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RpcFrameworkException("exception during discovery service: " + serviceName, e);
        }
    }

    public void finish() {
        zkClient.close();   
    }
}
