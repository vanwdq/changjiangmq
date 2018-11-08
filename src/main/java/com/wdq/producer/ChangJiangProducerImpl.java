package com.wdq.producer;

import com.wdq.rpc.client.ConnectManage;
import com.wdq.rpc.inter.IMqService;
import com.wdq.rpc.proxy.ProxyClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ChangJiangProducerImpl implements ChangJiangProducerInter {


    public ChangJiangProducerImpl(Properties properties) {
        String[] address = properties.getProperty("bootstrap.servers").split(":");
        String host = address[0];
        String port = address[1];
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ConnectManage.getInstance().connectServerNode(new InetSocketAddress(InetAddress.getByName(host), Integer.parseInt(port)), countDownLatch);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(ProducerRecord producerRecord) {
        try {
            IMqService iMqService = ProxyClient.create(IMqService.class);
            boolean flag = iMqService.deal(producerRecord);
            if (flag) {
                System.out.println("success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
