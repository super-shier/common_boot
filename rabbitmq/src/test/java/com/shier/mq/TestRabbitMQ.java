package com.shier.mq;

import com.shier.mq.modle.User;
import com.shier.mq.produce.HelloSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = App.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestRabbitMQ {

    @Autowired
    private HelloSender helloSender;

    @Test
    public void testRabbit() {
        helloSender.send("welecme to fula");
    }

    @Test
    public void sendRabbit() {
        User user = new User();
        user.setAge(18);
        user.setName("十二");
        user.setSchool("浙江工业大学");
        helloSender.sendUer(user);
    }
}