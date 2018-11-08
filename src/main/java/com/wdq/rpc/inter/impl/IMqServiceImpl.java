package com.wdq.rpc.inter.impl;


import com.wdq.consumer.ConsumerRecord;
import com.wdq.producer.ProducerRecord;
import com.wdq.rpc.inter.IMqService;
import com.wdq.rpc.inter.rpc.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RpcService(IMqService.class)
public class IMqServiceImpl implements IMqService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean deal(ProducerRecord producerRecord) {
        //入库
        String sql = "insert into tb_msg(msg_id,topic,body,status,create_time) values(?,?,?,?,?)";
        try {
            int flag = jdbcTemplate.update(sql,
                    producerRecord.getMsgId(),
                    producerRecord.getTopic(),
                    producerRecord.getMessage(),
                    0, new Date());
            if (flag > 0) {
                //落库成功 发送消息响应客户端
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ConsumerRecord> poll(List<String> topics, int size) {
        StringBuffer sb = new StringBuffer();
        if (topics != null && topics.size() > 0) {
            for (String topic : topics) {
                sb.append("'").append(topic).append("'").append(",");
            }
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        String sql = "select * from tb_msg where topic in (" + sb.toString() + ") limit 1";
        System.out.println("sql:" + sql);
        List<ConsumerRecord> consumerRecords = new ArrayList<>();
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                ConsumerRecord consumerRecord = new ConsumerRecord();
                String msgId = resultSet.getString("msg_id");
                String body = resultSet.getString("body");
                consumerRecord.setMsgId(msgId);
                consumerRecord.setValue(body);
                consumerRecords.add(consumerRecord);
            }
        });
        return consumerRecords;
    }
}
