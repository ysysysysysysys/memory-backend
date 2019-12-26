package com.yesong.memory.memory.service;

import com.yesong.memory.memory.request.UserRequest;
import com.yesong.memory.memory.response.CommonResponse;

/**
 * 用户账号相关
 */
public interface UserService {
    public CommonResponse checkAccount(String account);

    public CommonResponse regist(UserRequest userRequest);

    public CommonResponse login(UserRequest userRequest);
}
