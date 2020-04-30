package com.liugeng.rpcframework.exception;

public class RpcFrameworkException extends RuntimeException {

    public RpcFrameworkException(String message) {
        super(message);
    }

    public RpcFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcFrameworkException(Throwable cause) {
        super(cause);
    }
}
