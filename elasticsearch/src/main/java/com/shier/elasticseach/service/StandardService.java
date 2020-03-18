package com.shier.elasticseach.service;

import com.shier.elasticseach.model.Role;
import com.shier.elasticseach.model.User;

import java.util.List;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/5 11:06
 */
public interface StandardService {
     void addUser(User user);
     void deletedUserById(String id);
     void updateUser(User user) throws Exception;
     List<User> queryByUserName(String userName) ;
     List<User> queryByRoleName(Role role);
}
