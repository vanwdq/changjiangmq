package com.wdq.consumer;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ConsumerRecord implements Serializable {
    private String msgId;
    private String value;
}
