package com.springzhou.example.api;

import com.springzhou.example.service.RocketmqProducerService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhou
 * @create 2019-11-25 15:59
 * @description:
 */
@RestController
@RequestMapping("/roketmq")
public class RoketmqController {

    @Autowired
    RocketmqProducerService rocketmqProducerService;

    @GetMapping("/sendMsg")
    public Map sendMsg(HttpServletRequest request, String msg) {
        Map map = new HashMap();
        map.put("result",rocketmqProducerService.sendMsg(msg));
        return map;
    }

    @GetMapping("/sendMsgObj")
    public Map sendMsgObj(HttpServletRequest request, String msg) {
        Map map = new HashMap();
        map.put("result",rocketmqProducerService.sendMsgObj());
        return map;
    }

    @GetMapping("/sendMsgObjDelay")
    public Map sendMsgObjDelay(HttpServletRequest request, String msg) {
        Map map = new HashMap();
        map.put("result",rocketmqProducerService.sendMsgObjDelay(5));
        return map;
    }


    @GetMapping("/sendMsgAsy")
    public Map sendMsgAsy(HttpServletRequest request, String msg) {
        Map map = new HashMap();
        map.put("result",rocketmqProducerService.sendMsgAsy());
        return map;
    }

    @GetMapping("/sendMsgOneway")
    public Map sendMsgOneway(HttpServletRequest request, String msg) {
        Map map = new HashMap();
        map.put("result",rocketmqProducerService.sendMsgOneway());
        return map;
    }


}