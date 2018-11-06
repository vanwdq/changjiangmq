package com.wdq.rpc.handle;

import com.wdq.rpc.RpcBootstrap;
import com.wdq.rpc.serializable.RpcRequest;
import com.wdq.rpc.serializable.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务处理器
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private Map<String, Object> map = new HashMap<>();

    public ServerHandler(Map<String, Object> map) {
        this.map = map;
    }

    // 用于获取客户端发送的信息
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        RpcBootstrap.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 用于获取客户端发来的数据信息
                    RpcRequest body = (RpcRequest) msg;
                    RpcResponse rpcResponse = new RpcResponse();
                    String className = body.getClassName();
                    String methodName = body.getMethodName();
                    Object[] paramValue = body.getParamValue();
                    Class[] paramType = body.getParamType();
                    Object serviceBean = map.get(className);
                    if (serviceBean != null) {
                        FastClass serviceFastClass = FastClass.create(serviceBean.getClass());
                        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, paramType);
                        Object object = serviceFastMethod.invoke(serviceBean, paramValue);
                        rpcResponse.setResponseId(body.getRequestId());
                        rpcResponse.setValue(object);
                        rpcResponse.setStatus(200);
                        ctx.writeAndFlush(rpcResponse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }
}