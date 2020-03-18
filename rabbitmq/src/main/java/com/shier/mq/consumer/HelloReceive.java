package com.shier.mq.consumer;

import com.shier.mq.modle.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class HelloReceive {
    private Logger logger = LoggerFactory.getLogger(HelloReceive.class);

    @RabbitListener(queues = "queue")    //监听器监听指定的Queue
    public void processC(String str) {
        logger.info("*****************Receive:{}", str);
    }

    @RabbitListener(queues = "user")
    public void getUser(User user) {
        logger.info("*****************user:{}", user.toString());
    }

}