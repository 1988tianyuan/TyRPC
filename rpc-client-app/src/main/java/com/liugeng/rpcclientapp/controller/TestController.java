package com.liugeng.rpcclientapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.service.ExampleService;

@RestController
public class TestController {

    @Autowired
    private RpcProxy<ExampleService> proxy;

    @RequestMapping("/test")
    public String get() {
        ExampleService service = proxy.proxyInstance();
        String result = service.doSomething("我是第一个", "我是第二个");
        System.out.println("结果是： " + result);
        return result;
    }
}
