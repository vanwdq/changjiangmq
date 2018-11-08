package com.wdq.consumer;

import java.util.List;

public interface ChangJiangConsumerInter {
    List<ConsumerRecord> poll(int size);

    void subscribe(List<String> topics);
}
