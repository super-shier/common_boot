package com.shier.elasticseach.service.impl;

import com.shier.elasticseach.dao.ElasticOperationService;
import com.shier.elasticseach.model.Role;
import com.shier.elasticseach.model.User;
import com.shier.elasticseach.model.param.UserSearchParam;
import com.shier.elasticseach.service.StandardService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/5 11:03
 */
@Service
public class OrgUserServiceImpl implements StandardService, InitializingBean {

    @Autowired
    private ElasticOperationService elasticOperationService;

    private String index = "people";

    private String type = "man";


    @Override
    public void afterPropertiesSet() throws Exception {
        elasticOperationService.createIndexIfNotExist(index, type);
    }


    public void batchAddUser(List<User> users) {
        if(users.isEmpty()) {
            return ;
        }
        for(User user :users) {
            elasticOperationService.addDocumentToBulkProcessor(index, type, user);
        }
    }


    @Override
    public void addUser(User user) {
        elasticOperationService.addDocument(index, type, user);
    }

    @Override
    public void deletedUserById(String id) {
        elasticOperationService.deleteDocumentById(index, type, id);
    }

    @Override
    public void updateUser(User user) throws Exception {
        String id = user.getId();
        user.setId(null);
        elasticOperationService.updateDocument(index, type,id, user);
    }

    @Override
    public List<User> queryByUserName(String userName) {

        UserSearchParam param = new UserSearchParam();
        param.setUserName(userName);
        return elasticOperationService.queryDocumentByParam(index, type, param,User.class);
    }


    @Override
    public List<User> queryByRoleName(Role role) {
        UserSearchParam param = new UserSearchParam();
        param.setRoleName(role.getName());
        return elasticOperationService.queryDocumentByParam(index, type, param,User.class);
    }

}
