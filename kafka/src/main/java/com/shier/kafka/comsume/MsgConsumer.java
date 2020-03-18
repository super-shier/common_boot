package com.shier.kafka.comsume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MsgConsumer {

    private static final Logger log = LoggerFactory.getLogger(MsgConsumer.class);

    @KafkaListener(topics = {"topic-1", "topic-2"})
    public void processMessage(String content) {
        log.info("消息被消费content:{}", content);
    }

}