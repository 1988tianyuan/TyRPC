package com.liugeng.rpcclientapp.controller;

import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private ExampleService exampleService;

    @RequestMapping("/test")
    public String get() {
        String result = exampleService.doSomething("我是第一个", "我是第二个");
        System.out.println("结果是： " + result);
        return result;
    }
}
