package com.yesong.memory.memory.request;

import lombok.Data;

@Data
public class UserRequest {
    private String account;
    private String password;
    private String name;
    private String phone;
    private String sex;
    private String registCode;
}
