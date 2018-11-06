package com.wdq.rpc.proxy;

import com.wdq.rpc.client.ObjectProxy;
import com.wdq.rpc.serializable.RpcRequest;
import com.wdq.rpc.serializable.RpcResponse;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

public class ProxyClient {

    private RpcRequest rpcRequest;
    private RpcResponse rpcResponse;
    private Sync sync;

    public ProxyClient() {
        this.sync = new Sync();
    }

    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.rpcResponse != null) {
            return this.rpcResponse.getValue();
        } else {
            return null;
        }
    }

    public void done(RpcResponse reponse) {
        this.rpcResponse = reponse;
        sync.release(1);
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

    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires) {
            return getState() == done ? true : false;
        }

        protected boolean tryRelease(int releases) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }

}
