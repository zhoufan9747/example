package com.springzhou.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author zhou
 * @create 2019-11-26 15:32
 * @description:
 *Broker:它提供一种传输服务,它的角色就是维护一条从生产者到消费者的路线，保证数据能按照指定的方式进行传输,
 *Exchange：消息交换机,它指定消息按什么规则,路由到哪个队列。
 *Queue:消息的载体,每个消息都会被投到一个或多个队列。
 *Binding:绑定，它的作用就是把exchange和queue按照路由规则绑定起来.
 *Routing Key:路由关键字,exchange根据这个关键字进行消息投递。
 *vhost:虚拟主机,一个broker里可以有多个vhost，用作不同用户的权限分离。
 *Producer:消息生产者,就是投递消息的程序.
 *Consumer:消息消费者,就是接受消息的程序.
 *Channel:消息通道,在客户端的每个连接里,可建立多个channel.
 */
@Configuration
public class RabbitConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    /**
     * 普通队列
     */
    public final static String NOMAL_QUEUE_NAME = "nomal_queue_name";

    public final static String NOMAL_QUEUE_EXCHANGE = "nomal_queue_exchange";

    public final static String NOMAL_QUEUE_ROUTINGKEY= "nomal_queue_routingkey";

    /**
     * 普通队列
     * @return
     */
    @Bean
    public Queue nomalQueue() {
        return QueueBuilder.durable(NOMAL_QUEUE_NAME).build();
    }
    /**
     * 普通队列交换机
     * @return
     */
    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(NOMAL_QUEUE_EXCHANGE);
    }
    /**
     * 普通队列路由绑定
     * @return
     */
    @Bean
    public Binding binding(Queue nomalQueue,DirectExchange defaultExchange) {
        return BindingBuilder.bind(nomalQueue).to(defaultExchange).with(RabbitConfig.NOMAL_QUEUE_ROUTINGKEY);
    }


    /**
     * 发送到该队列的message会在一段时间后过期进入到delay_process_queue
     * 每个message可以控制自己的失效时间
     */
    public final static String DELAY_QUEUE_PER_MESSAGE_TTL_NAME = "delay_queue_per_message_ttl";

    /**
     * 发送到该队列的message会在一段时间后过期进入到delay_process_queue
     * 队列里所有的message都有统一的失效时间
     */
    public final static String DELAY_QUEUE_PER_QUEUE_TTL_NAME = "delay_queue_per_queue_ttl";
    public final static int QUEUE_EXPIRATION = 4000;

    /**
     * message失效后进入的队列，也就是实际的消费队列
     */
    public final static String DELAY_PROCESS_QUEUE_NAME = "delay_process_queue";

    /**
     * 实际消费队列的exchange
     */
    public final static String DELAY_EXCHANGE_NAME = "delay_exchange";

    /**
     * 路由到delay_queue_per_queue_ttl（统一失效时间的队列）的exchange（用于队列延迟重试）
     */
    public final static String PER_QUEUE_TTL_EXCHANGE_NAME = "per_queue_ttl_exchange";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true);
        return connectionFactory;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    /**
     * 创建DLX exchange
     *
     * @return
     */
    @Bean
    DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    /**
     * 创建per_queue_ttl_exchange
     *
     * @return
     */
    @Bean
    DirectExchange perQueueTTLExchange() {
        return new DirectExchange(PER_QUEUE_TTL_EXCHANGE_NAME);
    }

    /**
     * 创建delay_queue_per_message_ttl队列
     *
     * @return
     */
    @Bean
    Queue delayQueuePerMessageTTL() {
        return QueueBuilder.durable(DELAY_QUEUE_PER_MESSAGE_TTL_NAME)
                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME) // DLX，dead letter发送到的exchange
                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME) // dead letter携带的routing key
                .build();
    }

    /**
     * 创建delay_queue_per_queue_ttl队列
     *
     * @return
     */
    @Bean
    Queue delayQueuePerQueueTTL() {
        return QueueBuilder.durable(DELAY_QUEUE_PER_QUEUE_TTL_NAME)
                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME) // DLX
                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME) // dead letter携带的routing key
                .withArgument("x-message-ttl", QUEUE_EXPIRATION) // 设置队列的过期时间
                .build();
    }

    /**
     * 创建delay_process_queue队列，也就是实际消费队列
     *
     * @return
     */
    @Bean
    Queue delayProcessQueue() {
        return QueueBuilder.durable(DELAY_PROCESS_QUEUE_NAME)
                .build();
    }

    /**
     * 将DLX绑定到实际消费队列
     *
     * @param delayProcessQueue
     * @param delayExchange
     * @return
     */
    @Bean
    Binding dlxBinding(Queue delayProcessQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayProcessQueue)
                .to(delayExchange)
                .with(DELAY_PROCESS_QUEUE_NAME);
    }

    /**
     * 将per_queue_ttl_exchange绑定到delay_queue_per_queue_ttl队列（统一失效时间，用于队列延迟重试）
     *
     * @param delayQueuePerQueueTTL
     * @param perQueueTTLExchange
     * @return
     */
    @Bean
    Binding queueTTLBinding(Queue delayQueuePerQueueTTL, DirectExchange perQueueTTLExchange) {
        return BindingBuilder.bind(delayQueuePerQueueTTL)
                .to(perQueueTTLExchange)
                .with(DELAY_QUEUE_PER_QUEUE_TTL_NAME);
    }

}