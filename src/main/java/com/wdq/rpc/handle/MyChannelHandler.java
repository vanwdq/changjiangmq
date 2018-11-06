package com.wdq.rpc.handle;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 网络事件处理器
 */
public class MyChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 添加自定义的编码器和解码器
        // 添加POJO对象解码器 禁止缓存类加载器
        ch.pipeline().addLast(
                new ObjectDecoder(1024, ClassResolvers.cacheDisabled(this
                        .getClass().getClassLoader())));
        // 设置发送消息编码器
        ch.pipeline().addLast(new ObjectEncoder());
        // 处理网络IO
        ch.pipeline().addLast(new ClientHandler());// 处理网络IO
    }
}