package com.liugeng.rpcframework.rpcserver.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {

    Class<?> value();
}
