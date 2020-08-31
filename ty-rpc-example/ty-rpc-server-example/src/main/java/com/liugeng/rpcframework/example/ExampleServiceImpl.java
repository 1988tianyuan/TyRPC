package com.liugeng.rpcframework.example;

import com.liugeng.rpcframework.rpcserver.annotation.RpcService;

@RpcService(value = ExampleService.class)
public class ExampleServiceImpl implements ExampleService {
    @Override
    public String doSomething(String var1, String var2) {
        return "你输入的文字是：" + var1 + " 和 " + var2;
    }
}
