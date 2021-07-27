package com.cas.po;

/**
 * @author xiang_long
 * @version 1.0
 * @date 2021/7/27 5:27 下午
 * @desc
 */
public class User {

    private String name;

    private Integer age;

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
