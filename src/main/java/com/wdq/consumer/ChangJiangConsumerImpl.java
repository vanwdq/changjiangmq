package com.wdq.consumer;

import com.wdq.rpc.client.ConnectManage;
import com.wdq.rpc.inter.IMqService;
import com.wdq.rpc.proxy.ProxyClient;
import io.netty.channel.ChannelFuture;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ChangJiangConsumerImpl implements ChangJiangConsumerInter {

    private List<String> topics = new ArrayList<>();

    public ChangJiangConsumerImpl(Properties properties) {
        String[] address = properties.getProperty("bootstrap.servers").split(":");
        String host = address[0];
        String port = address[1];
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ConnectManage.getInstance().connectServerNode(new InetSocketAddress(InetAddress.getByName(host), Integer.parseInt(port)),countDownLatch);
            countDownLatch.await();
            System.out.println("初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ConsumerRecord> poll(int size) {
        IMqService iMqService = ProxyClient.create(IMqService.class);
        List<ConsumerRecord> consumerRecords = iMqService.poll(topics, size);
        return consumerRecords;
    }

    @Override
    public void subscribe(List<String> topics) {
        this.topics = topics;
    }
}
