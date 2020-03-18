package com.shier.elasticseach.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/6 10:15
 */
@Data
@ToString
public class Man  implements Serializable {
    private String id;
    private Integer age;
    private Date date;
    private String name;
    private String country;
}
