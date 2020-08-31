package com.liugeng.rpcframework.rpcserverapp.rpcserver;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.liugeng.rpcframework.registry.ServiceRegister;
import com.liugeng.rpcframework.rpcserver.annotation.RpcService;
import com.liugeng.rpcframework.rpcserver.server.RpcServer;

public class RpcServerInSpring extends RpcServer implements ApplicationContextAware, InitializingBean, DisposableBean{

    public RpcServerInSpring(String serviceName, String rpcAddress, ServiceRegister serviceRegister) {
        super(serviceName, rpcAddress, serviceRegister);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String serviceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                loadService(serviceName, serviceBean);
            }
        }
        logger.info("服务初始化完成，其中可调用的服务有：" + serviceMap.keySet());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startServer();
    }

    @Override
    public void destroy() throws Exception {
        shutDown();
    }
}
