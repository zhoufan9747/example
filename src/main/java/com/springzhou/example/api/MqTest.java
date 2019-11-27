package com.springzhou.example.api;

import com.springzhou.example.service.rabbitMq.MsgProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhou
 * @create 2019-11-26 16:00
 * @description:
 */
@RestController
public class MqTest {
    @Autowired
    MsgProducer msgProducer;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/test/MqTest")
    public String MqTest(HttpServletRequest request, String msg) {
        try {
            logger.info("创建消息");
            msgProducer.sendMsg(msg);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  "成功";
    }

    @RequestMapping(value = "/test/MqTestDelayTime")
    public String MqTestDelayTime(HttpServletRequest request, String msg, Long time) {
        try {
            logger.info("创建消息,延迟消费");
            msgProducer.sendMsgDelayTime(msg,time);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  "成功";
    }

    @RequestMapping(value = "/test/MqTestDelay")
    public String MqTestDelayQueue(HttpServletRequest request, String msg) {
        try {
            logger.info("创建消息,延迟消费");
            msgProducer.sendMsgDelay(msg);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  "成功";
    }

}