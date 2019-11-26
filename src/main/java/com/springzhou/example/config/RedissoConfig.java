package com.springzhou.example.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * redisson配置
 *
 * @author
 * @create 2019-08-22
 */
@Configuration
@RefreshScope
public class RedissoConfig {
    /**
     * https://github.com/redisson/redisson/wiki/8.-%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81%E5%92%8C%E5%90%8C%E6%AD%A5%E5%99%A8
     * // 加锁以后10秒钟自动解锁
     * // 无需调用unlock方法手动解锁
     * lock.lock(10, TimeUnit.SECONDS);
     *
     * // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
     * boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
     * if (res) {
     *    try {
     *      ...
     *    } finally {
     *        lock.unlock();
     *    }
     * }
     * @param configFile
     * @return
     * @throws IOException
     */
    @Value("${spring.redis.host}")
    private String redis_host;
    @Value("${spring.redis.port}")
    private String redis_port;
    @Value("${spring.redis.password}")
    private String redis_password;

    @Bean(destroyMethod = "shutdown", name = "redissonClient")
    public RedissonClient redissonClient() throws IOException {
        Config config = new Config();
        System.out.println("redissonClient初始化_"+"redis://"+redis_host+":"+redis_port);
        config.useSingleServer()
                //可以用"rediss://"来启用SSL连接
                .setAddress("redis://"+redis_host+":"+redis_port)
                .setPassword(redis_password).setDatabase(1);
        return Redisson.create(config);
    }
}
