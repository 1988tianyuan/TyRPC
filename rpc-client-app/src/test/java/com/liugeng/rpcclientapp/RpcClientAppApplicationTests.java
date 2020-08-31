package com.liugeng.rpcclientapp;

import com.liugeng.rpcframework.rpcclient.proxy.RpcProxy;
import com.liugeng.rpcframework.service.ExampleService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {RpcClientAppApplication.class})
public class RpcClientAppApplicationTests {

	@Autowired
	private ExampleService exampleService;

	@Test
	public void test1() {
		String result = exampleService.doSomething("我是第一个", "我是第二个");
		System.out.println("结果是： " + result);
	}

}
