package com.shier.mq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderConf {
    @Bean(name = "queue")
    public Queue queue() {
        return new Queue("queue");
    }

    @Bean(name = "user")
    public Queue user() {
        return new Queue("user");
    }
}