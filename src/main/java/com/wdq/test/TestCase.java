package com.wdq.test;

import com.wdq.producer.ChangJiangProducerImpl;
import com.wdq.producer.ChangJiangProducerInter;
import com.wdq.producer.ProducerRecord;
import org.springframework.util.StopWatch;

import java.util.Properties;

public class TestCase {


    public static void main(String[] args) {
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
