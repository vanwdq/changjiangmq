package com.wdq.rpc.inter.impl;


import com.wdq.producer.ProducerRecord;
import com.wdq.rpc.inter.IMqService;
import com.wdq.rpc.inter.rpc.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

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
}
