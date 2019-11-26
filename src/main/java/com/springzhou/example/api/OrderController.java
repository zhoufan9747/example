package com.springzhou.example.api;

import com.netflix.client.http.HttpRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author zhou
 * @create 2019-08-19 16:48
 * @description:
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    @Value("${server.port}")
    private String port;
    @Autowired
    RedissonClient redissonClient;

    /**
     * 获取服务端口号
     * @return
     */
    @GetMapping("/getOrderPort")
    public String getOrderPort(HttpServletRequest request) {
        System.out.println(request.getHeader("X-Request-Foo"));
        System.out.println(request.getHeader("zhou"));
        return "order-service port：" + port;
    }

    @GetMapping("/getOrderPortLock")
    public String getOrderPortLock(HttpServletRequest request) {
        System.out.println(request.getHeader("X-Request-Foo"));
        System.out.println(request.getHeader("zhou"));

        // 1. 获得锁对象
        RLock lock = redissonClient.getLock("getOrderPortLock");
        //调用业务
        try {
            //加锁
            //尝试加锁，最多等待100秒，上锁以后10秒自动解锁
            boolean isLock= lock.tryLock(3, 60, TimeUnit.SECONDS);
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //解锁
            lock.unlock();
        }

        return "order-service port：" + port;
    }



}