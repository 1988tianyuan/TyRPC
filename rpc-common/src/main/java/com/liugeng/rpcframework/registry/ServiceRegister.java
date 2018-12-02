package com.liugeng.rpcframework.registry;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.exception.RpcFrameworkException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceRegister {

    private String zkAddress;
    private final RetryPolicy retryPolicy;
    private final CuratorFramework zkClient;
    private final String serviceName;
    private static Logger logger = LoggerFactory.getLogger(ServiceRegister.class);

    public ServiceRegister(String zkAddress, String serviceName) {
        this.zkAddress = zkAddress;
        this.serviceName = "/" + serviceName;
        this.retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.zkClient = CuratorFrameworkFactory.newClient(zkAddress, retryPolicy);
        this.zkClient.start();
    }

    public void register(String serviceAddress) {
        Preconditions.checkNotNull(serviceName, "servicePath should not be null !");
        Preconditions.checkNotNull(zkClient, "zkClient is not be initialized !");
        String servicePath = serviceName + "/" + serviceAddress;
        try {
            if (zkClient.checkExists().forPath(serviceName) == null) {
                zkClient.create().forPath(serviceName, serviceName.getBytes());
            }
            if (zkClient.checkExists().forPath(servicePath) == null) {
                zkClient.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath, serviceAddress.getBytes());
            }
            logger.info("success to register on service: {}", serviceName);
        } catch (Exception e) {
            logger.error("failed to register service on zookeeper", e);
            throw new RpcFrameworkException("failed to register service on zookeeper", e);
        }
    }

    public void stopZkClient() {
        this.zkClient.close();
    }





}
