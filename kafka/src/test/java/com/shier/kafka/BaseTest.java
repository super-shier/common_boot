package com.shier.kafka;


import com.shier.kafka.product.MsgProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 描述: 测试 kafka
 *
 * @author yanpenglei
 * @create 2017-10-16 18:45
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Startup.class)
public class BaseTest {

    @Autowired
    private MsgProducer msgProducer;

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++) {
            msgProducer.sendMessage("topic-1", "topic--------1******" + i);
            msgProducer.sendMessage("topic-2", "topic--------2******" + i);
        }
    }
}
