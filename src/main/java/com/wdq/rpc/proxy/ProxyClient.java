package com.wdq.rpc.proxy;

import com.wdq.rpc.client.ObjectProxy;
import com.wdq.rpc.serializable.RpcRequest;
import com.wdq.rpc.serializable.RpcResponse;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;

public class ProxyClient {

    private RpcRequest rpcRequest;
    private RpcResponse rpcResponse;
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public Object get() throws InterruptedException {
        countDownLatch.await();
        if (this.rpcResponse != null) {
            return this.rpcResponse.getValue();
        }
        return null;
    }

    public void done(RpcResponse reponse) {
        this.rpcResponse = reponse;
        countDownLatch.countDown();
    }

    public RpcRequest getRpcRequest() {
        return rpcRequest;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ObjectProxy<T>()
        );
    }
}
