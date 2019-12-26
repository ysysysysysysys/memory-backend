package com.yesong.memory.memory.action;

import com.yesong.memory.memory.annotations.Auth;
import com.yesong.memory.memory.request.UserRequest;
import com.yesong.memory.memory.response.CommonResponse;
import com.yesong.memory.memory.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api(value = "用户",description = "用户端")
public class UserAction {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/checkAccount",method = {RequestMethod.POST})
    @ApiOperation(value = "校验用户")
    public CommonResponse checkAccount(String account){
        return userService.checkAccount(account);
    }

    @RequestMapping(value = "/regist",method = {RequestMethod.POST})
    @ApiOperation(value = "注册")
    public CommonResponse regist(@RequestBody UserRequest userRequest){
        return userService.regist(userRequest);
    }
    @RequestMapping(value = "/login",method = {RequestMethod.POST})
    @ApiOperation(value = "登录")
    @Auth
    public CommonResponse login(@RequestBody UserRequest userRequest){
        return userService.login(userRequest);
    }



}
