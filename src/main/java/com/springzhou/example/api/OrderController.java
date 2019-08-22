package com.springzhou.example.api;

import com.netflix.client.http.HttpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

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
}