package com.shier.elasticseach.service;

import com.shier.elasticseach.model.Man;

import java.util.List;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/6 10:20
 */
public interface UserService {
    void addMan(Man man);
    void deletedManById(String id);
    void updateMan(Man man) throws Exception;
    Man queryById(String id) ;
    List<Man> queryByManName(String name) ;
    String createIndex(String index, String type);
}
