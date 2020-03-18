package com.shier.elasticseach.controller;

import com.alibaba.fastjson.JSONObject;
import com.shier.elasticseach.model.Man;
import com.shier.elasticseach.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/5 12:48
 */
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public String add(@RequestBody Man man){
        userService.addMan(man);
        return "SUCCESS";
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam String id){
        userService.deletedManById(id);
        return "SUCCESS";
    }

    @PutMapping("/update")
    public String update(@RequestBody Man man){
        try {
            userService.updateMan(man);
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
        return "SUCCESS";
    }

    @GetMapping("/detail")
    public String detail(@RequestParam String id){
        return JSONObject.toJSONString(userService.queryById(id));
    }

    @GetMapping("/query")
    public List<Man> query(@RequestParam String name){
        return userService.queryByManName(name);
    }

    @PostMapping("/index")
    public String index(@RequestParam String index,String type){
        return userService.createIndex(index,type);
    }
}
