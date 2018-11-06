package com.wdq.rpc.inter;

import com.wdq.producer.ProducerRecord;

public interface IMqService {
    boolean deal(ProducerRecord producerRecord);
}
