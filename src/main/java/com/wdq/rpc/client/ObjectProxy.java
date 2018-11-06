package com.wdq.rpc.client;

import com.wdq.rpc.handle.ClientHandler;
import com.wdq.rpc.proxy.ProxyClient;
import com.wdq.rpc.serializable.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class ObjectProxy<T> implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getDeclaringClass().getName());
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParamType(method.getParameterTypes());
        rpcRequest.setParamValue(args);
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        ClientHandler clientHandler = ConnectManage.getInstance().chooseHandler();
        if (clientHandler != null) {
            ProxyClient proxyClient = clientHandler.sendRequest(rpcRequest);
            return proxyClient.get();
        }
        return null;
    }


}
