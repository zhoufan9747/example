package com.springzhou.example.service;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhou
 * @create 2019-11-26 14:46
 * @description:
 */
public class Producer {
    public static void main(String[] args) throws MQClientException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer("ProducerGroupName");
        producer.setNamesrvAddr("192.168.161.3:9876");
        producer.start();

        for (int i = 0; i < 1; i++)
            try {
                {
                    Message msg = new Message("test-topic-1",
                            "TagA",
                            "OrderID188",
                            "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                    msg.setDelayTimeLevel(3);//设置延迟队列的level，5表示延迟一分钟
                    SendResult sendResult = producer.send(msg);
                    System.out.printf("%s%n", sendResult);
                    System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

//        producer.shutdown();
    }
}