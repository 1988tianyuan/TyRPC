package com.liugeng.rpcframework.rpcclient.client;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerType;
import com.liugeng.rpcframework.rpcclient.client.lb.RandomStrategy;
import com.liugeng.rpcframework.rpcclient.client.service.ServiceDiscovery;
import com.liugeng.rpcframework.rpcclient.client.lb.LoadBalancerStrategy;

public class ServiceDiscoverClient extends AbstractRpcClient {
    private static Logger logger = LoggerFactory.getLogger(ServiceDiscoverClient.class);
    private String rpcServiceName;
    private final ServiceDiscovery serviceDiscovery;
    private final LoadBalancerStrategy lbStrategy;

    public ServiceDiscoverClient(String rpcServiceName, ServiceDiscovery serviceDiscovery, LoadBalancerType lbType) {
        this.serviceDiscovery = serviceDiscovery;
        this.rpcServiceName = rpcServiceName;
        this.lbStrategy = lbType != null ? lbType.getLoadBalancerStrategy() : new RandomStrategy();
    }

    @Override
    public String chooseAddress() {
        List<RpcInstance> instanceList = serviceDiscovery.discovery(rpcServiceName);
        RpcInstance rpcInstance = lbStrategy.selectInstance(instanceList);
        Preconditions.checkArgument(rpcInstance != null && StringUtils.isNotBlank(rpcInstance.getAddress()), 
            "no rpc server is available for rpcService: " + rpcServiceName);
        return rpcInstance.getAddress();
    }
}
