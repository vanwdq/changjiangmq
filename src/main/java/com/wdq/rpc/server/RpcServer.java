package com.wdq.rpc.server;

import com.wdq.rpc.handle.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.HashMap;
import java.util.Map;

public class RpcServer {

    private String serverPort;

    public RpcServer(String serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * netty 启动服务端 并做好序列化以及在handle中进行业务处理,业务通过线程池方式
     */
    public void start(Map<String, Object> map) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            //实例化serversocketchannel
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChildChannelHandler(map));
            // ChannelFuture：代表异步I/O的结果
            ChannelFuture f = bootstrap.bind(Integer.parseInt(serverPort)).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }

    /**
     * 网络事件处理器
     */
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        private Map<String, Object> map = new HashMap<>();

        public ChildChannelHandler(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            // 添加对象解码器 负责对序列化POJO对象进行解码 设置对象序列化最大长度为1M 防止内存溢出
            // 设置线程安全的WeakReferenceMap对类加载器进行缓存 支持多线程并发访问 防止内存溢出
            ch.pipeline().addLast(
                    new ObjectDecoder(1024 * 1024, ClassResolvers
                            .weakCachingConcurrentResolver(this.getClass()
                                    .getClassLoader())));
            // 添加对象编码器 在服务器对外发送消息的时候自动将实现序列化的POJO对象编码
            ch.pipeline().addLast(new ObjectEncoder());
            //    deal();
            // 处理网络IO
            ch.pipeline().addLast(new ServerHandler(map));
        }
    }

}

