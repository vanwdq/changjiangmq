package com.wdq.rpc;

import com.wdq.rpc.inter.rpc.RpcService;
import com.wdq.rpc.server.RpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcBootstrap {

    private static ThreadPoolExecutor threadPoolExecutor;

    public static void submit(Runnable task) {
        if (threadPoolExecutor == null) {
            synchronized (RpcBootstrap.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(8, 8, 600l, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }


    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-rpc-server.xml");
        RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (Object serviceBean : serviceBeanMap.values()) {
            try {
                // 获取自定义注解上的value
                String name = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                map.put(name, serviceBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        rpcServer.start(map);
    }
}
