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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectManage {
    private volatile static ConnectManage connectManage;
    private CopyOnWriteArrayList<ClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long connectTimeoutMillis = 6000;

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


    public void connectServerNode(final InetSocketAddress remotePeer) {
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
                    ClientHandler clientHandler = channelFuture.channel().pipeline().get(ClientHandler.class);
                    addHandler(clientHandler);
                }
            }
        });
    }

    private void signalAvailableHandler() {
        lock.lock();
        try {
            connected.signalAll();
        } finally {
            lock.unlock();
        }
    }

    private boolean waitingForHandler() throws InterruptedException {
        lock.lock();
        try {
            return connected.await(this.connectTimeoutMillis, TimeUnit.MILLISECONDS);
        } finally {
            lock.unlock();
        }
    }


    public ClientHandler chooseHandler() {
        if (connectedHandlers != null && connectedHandlers.size() > 0) {
            return connectedHandlers.get(0);
        }else {
            System.out.println("sasasas");
        }
        return null;
    }


    private void addHandler(ClientHandler handler) {
        connectedHandlers.add(handler);
        signalAvailableHandler();
    }


}
