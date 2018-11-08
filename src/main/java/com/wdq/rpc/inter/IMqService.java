package com.wdq.rpc.inter;

import com.wdq.consumer.ConsumerRecord;
import com.wdq.producer.ProducerRecord;

import java.util.List;

public interface IMqService {
    boolean deal(ProducerRecord producerRecord);

    List<ConsumerRecord> poll(List<String> topics, int size);
}
