package com.shier.elasticseach.model.param;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: liyunbiao
 * @Date: 2019/5/6 10:28
 */
@Data
@ToString
public class ManSearchParam  implements Serializable {
    private CharSequence  id;
    private CharSequence  country;
    private Integer age;
    private CharSequence name;
}
