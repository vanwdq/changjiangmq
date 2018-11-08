package com.wdq.rpc.client;

import com.wdq.rpc.handle.ClientHandler;
import com.wdq.rpc.handle.MyChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

public class ConnectManage {
    private volatile static ConnectManage connectManage;
    private CopyOnWriteArrayList<ClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();

    private ConnectManage() {

    }

    public static ConnectManage getInstance() {
        if (connectManage == null) {
            synchronized (ConnectManage.class) {
                if (connectManage == null) {
                    connectManage = new ConnectManage();
                }
            }
        }
        return connectManage;
    }


    public void connectServerNode(final InetSocketAddress remotePeer, CountDownLatch countDownLatch) {
        Bootstrap b = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new MyChannelHandler());

        ChannelFuture channelFuture = b.connect(remotePeer);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                   // System.out.println("连接成功");
                    ClientHandler clientHandler = channelFuture.channel().pipeline().get(ClientHandler.class);
                    addHandler(clientHandler);
                    countDownLatch.countDown();
                }
            }
        });
    }

    public ClientHandler chooseHandler() {
        if (connectedHandlers != null && connectedHandlers.size() > 0) {
            return connectedHandlers.get(0);
        } else {
            System.out.println("连接失败");
        }
        return null;
    }


    private void addHandler(ClientHandler handler) {
        connectedHandlers.add(handler);
    }


}
