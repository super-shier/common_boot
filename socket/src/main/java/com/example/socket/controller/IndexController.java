package com.example.socket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private SocketService socketService;

    @RequestMapping("index")
    public String index() {
        logger.info("=================index");
        return "index";
    }

    @ResponseBody
    @RequestMapping("send")
    public String sendSorketMsg(String msg, String name) {
        socketService.sendToUser(msg + "|" + name);
        return "success";
    }
}
