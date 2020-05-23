package com.liugeng.rpcclientapp.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.service.ExampleService;

@RestController
public class TestController {

    @Resource
    private RpcProxy<ExampleService> exampleService;

    @RequestMapping("/test")
    public String get() {
        ExampleService service = exampleService.proxyInstance();
        return service.doSomething("我是第一个", "我是第二个");
    }
}
