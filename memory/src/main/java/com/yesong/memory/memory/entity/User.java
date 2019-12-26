package com.yesong.memory.memory.entity;

import lombok.Data;

@Data
public class User extends BaseEntity<User> {
    private String account;
    private String password;
    private String name;
    private String phone;
    private String sex;
}
