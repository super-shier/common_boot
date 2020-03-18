package com.shier.elasticseach;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/5 11:06
 */
@SpringBootApplication
public class ElasticsearchApplication {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchApplication.class, args);
        logger.info("start completed ！");
    }

}
