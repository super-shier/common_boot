package com.shier.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisUtils redisUtils;

    @Test
    public void redisTest() {
        redisUtils.set("123", "hello world shier");
        System.out.println("进入了方法");
        String string = redisUtils.get("123").toString();
        System.out.println("string===========:" + string);
    }
}
