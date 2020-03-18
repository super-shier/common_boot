package com.shier.elasticseach.model.param;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/5 9:54
 */
public class UserSearchParam {

    private CharSequence  roleName;
    private CharSequence description;
    private Integer age;
    private CharSequence userName;

    public CharSequence getRoleName() {
        return roleName;
    }

    public void setRoleName(CharSequence roleName) {
        this.roleName = roleName;
    }

    public CharSequence getDescription() {
        return description;
    }

    public void setDescription(CharSequence description) {
        this.description = description;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public CharSequence getUserName() {
        return userName;
    }

    public void setUserName(CharSequence userName) {
        this.userName = userName;
    }
}
