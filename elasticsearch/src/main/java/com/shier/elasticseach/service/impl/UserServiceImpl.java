package com.shier.elasticseach.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shier.elasticseach.dao.ElasticOperationService;
import com.shier.elasticseach.model.Man;
import com.shier.elasticseach.model.param.ManSearchParam;
import com.shier.elasticseach.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/6 10:22
 */
@Service
public class UserServiceImpl implements UserService, InitializingBean {
    @Autowired
    private ElasticOperationService elasticOperationService;

    private String index = "people";

    private String type = "man";
    @Override
    public void addMan(Man man) {
            elasticOperationService.addDocument(index, type, man);
    }

    @Override
    public void deletedManById(String id) {
        elasticOperationService.deleteDocumentById(index, type, id);
    }

    @Override
    public void updateMan(Man man) throws Exception {
        String id = man.getId();
        man.setId(null);
        elasticOperationService.updateDocument(index, type,id, man);
    }

    @Override
    public Man queryById(String id) {
        Object object=elasticOperationService.queryDocumentById(index,type,id);
        return JSONObject.toJavaObject((JSON) JSONObject.toJSON(object),Man.class);
    }

    @Override
    public List<Man> queryByManName(String name) {
        ManSearchParam param = new ManSearchParam();
        param.setName(name);
        return elasticOperationService.queryDocumentByManParam(index, type, param, Man.class);
    }

    @Override
    public String createIndex(String index, String type) {
        return String.valueOf(elasticOperationService.createIndex(index,type));
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        elasticOperationService.createIndexIfNotExist(index, type);
    }
}
