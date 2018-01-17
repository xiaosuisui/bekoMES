package com.mj.beko.domain;

import com.mj.beko.util.JsonReadingProperty;
import lombok.Data;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Created by Ricardo on 2017/11/8.
 * 该实体注册成员工账号管理，如果记录了信息则不在请求
 */
@Data
public class EmployInfo implements Serializable {
    @JsonReadingProperty("EmployeeNumber")
    private String employeeNumber;
    @JsonReadingProperty("Name")
    private String name;
    @JsonReadingProperty("Surname")
    private String surname;

    @Override
    public String toString() {
        return "employInfo{" +
                "employeeNumber='" + employeeNumber + '\'' +
                ", name='" + name + '\'' +
                ", Surname='" + surname + '\'' +
                '}';
    }
}
