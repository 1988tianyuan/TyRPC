package com.liugeng.rpcframework.rpcclient.client;

import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.registry.ServiceDiscovery;

public class ServiceDiscoverClient extends AbstractRpcClient {
    private static Logger logger = LoggerFactory.getLogger(ServiceDiscoverClient.class);
    private String rpcServiceName;
    private final ServiceDiscovery serviceDiscovery;

    public ServiceDiscoverClient(String rpcServiceName, ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcServiceName = rpcServiceName;
    }

    @Override
    public String chooseAddress() {
        List<String> addressList = serviceDiscovery.discovery(rpcServiceName);
        Preconditions.checkArgument(!addressList.isEmpty(),
                "rpc server: {} is not available now !", rpcServiceName);
        int size = addressList.size();
        int index = new Random().nextInt(size);
        return addressList.get(index);
    }
}
