package com.wdq.producer;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ProducerRecord implements Serializable {
    private String msgId;
    private String topic;
    private String message;

    public ProducerRecord() {
    }

    public ProducerRecord(String topic, String message) {
        this.msgId = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        this.topic = topic;
        this.message = message;
    }
}
