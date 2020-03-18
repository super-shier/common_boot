package com.shier.elasticseach.model;

import java.util.Date;
import java.util.List;

/**
 * 声明 USER实体，注意有一个1对多关系的roles
 **/
public class User {


    private String id;


    private String userName;


    private Integer age;


    private Date birthday;


    private String description;

    /**
     * 1对多在spring-data-elasticsearch 统一为nested类型
     **/
    private List<Role> roles;


    public User() {}

    public User(String userName,Integer age,Date birthday,String description) {
        this.userName = userName;
        this.age = age;
        this.birthday = birthday;
        this.description = description;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }



    public Date getBirthday() {
        return birthday;
    }



    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    public Integer getAge() {
        return age;
    }


    public void setAge(Integer age) {
        this.age = age;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<Role> getRoles() {
        return roles;
    }


    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


}
