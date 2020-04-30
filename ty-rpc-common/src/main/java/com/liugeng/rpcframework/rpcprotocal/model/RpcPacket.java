package com.liugeng.rpcframework.rpcprotocal.model;


public abstract class RpcPacket {

    private Byte version = 1;

    public abstract Byte getCommand();

    public Byte getVersion() {
        return version;
    }

    public void setVersion(Byte version) {
        this.version = version;
    }
}
