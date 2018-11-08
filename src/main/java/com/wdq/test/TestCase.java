package com.wdq.test;

import com.wdq.consumer.ChangJiangConsumerImpl;
import com.wdq.consumer.ChangJiangConsumerInter;
import com.wdq.consumer.ConsumerRecord;
import com.wdq.producer.ChangJiangProducerImpl;
import com.wdq.producer.ChangJiangProducerInter;
import com.wdq.producer.ProducerRecord;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TestCase {


    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:8000");
        ChangJiangConsumerInter changJiangConsumerInter = new ChangJiangConsumerImpl(props);
        changJiangConsumerInter.subscribe(Arrays.asList("hello"));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<ConsumerRecord> msgList = new ArrayList();
        for (; ; ) {
            msgList = changJiangConsumerInter.poll(1000);
            if (null != msgList && msgList.size() > 0) {
                for (ConsumerRecord consumerRecord : msgList) {
                    System.out.println(consumerRecord);
                }
            }
            Thread.sleep(50);
        }
    }

    private static void produce() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:8000");
        ChangJiangProducerInter changJiangProducerInter = new ChangJiangProducerImpl(props);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10000; i++) {
            changJiangProducerInter.send(new ProducerRecord("hello", "world"));
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());
    }
}
