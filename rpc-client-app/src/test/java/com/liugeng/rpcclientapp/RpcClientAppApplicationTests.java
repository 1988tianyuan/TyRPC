package com.liugeng.rpcclientapp;

import com.liugeng.rpcframework.rpcclient.RpcClient;
import com.liugeng.rpcframework.rpcclient.RpcProxy;
import com.liugeng.rpcframework.service.ExampleService;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RpcClientAppApplication.class})
public class RpcClientAppApplicationTests {

	@Autowired
	private RpcProxy rpcProxy;

	@Test
	public void test1() {
		ExampleService exampleService = rpcProxy.createService(ExampleService.class);
		String result = exampleService.doSomething("我是第一个", "我是第二个");
		System.out.println("结果是： " + result);
	}

}
