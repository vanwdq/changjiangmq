package com.wdq.rpc.handle;

import com.wdq.rpc.proxy.ProxyClient;
import com.wdq.rpc.serializable.RpcRequest;
import com.wdq.rpc.serializable.RpcResponse;
import io.netty.channel.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

//用于读取客户端发来的信息
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private Channel channel;
    private ConcurrentHashMap<String, ProxyClient> pendingRPC = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            RpcResponse response = (RpcResponse) msg;
            String requestId = response.getResponseId();
            ProxyClient rpcFuture = pendingRPC.get(requestId);
            if (rpcFuture != null) {
                pendingRPC.remove(requestId);
                rpcFuture.done(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    public ProxyClient sendRequest(RpcRequest request) {
        final CountDownLatch latch = new CountDownLatch(1);
        ProxyClient proxyClient = new ProxyClient();
        pendingRPC.put(request.getRequestId(), proxyClient);
        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
        }
        return proxyClient;
    }


}
