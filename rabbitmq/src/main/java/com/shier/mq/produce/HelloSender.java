package com.shier.mq.produce;

import com.shier.mq.modle.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelloSender {
    @Autowired
    private AmqpTemplate template;

    public void send(String msg) {
        template.convertAndSend("queue", msg);
    }

    public void sendUer(User user) {
        template.convertAndSend("user", user);
    }

}