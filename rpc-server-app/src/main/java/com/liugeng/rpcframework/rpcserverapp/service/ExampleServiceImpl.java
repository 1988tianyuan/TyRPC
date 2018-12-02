package com.liugeng.rpcframework.rpcserverapp.service;

import com.liugeng.rpcframework.rpcserver.annotation.RpcService;
import com.liugeng.rpcframework.service.ExampleService;
import org.springframework.stereotype.Service;

@RpcService(ExampleService.class)
@Service
public class ExampleServiceImpl implements ExampleService {
    @Override
    public String doSomething(String var1, String var2) {
        return "你输入的文字是：" + var1 + " 和 " + var2;
    }
}
