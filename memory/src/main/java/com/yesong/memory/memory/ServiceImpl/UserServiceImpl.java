package com.yesong.memory.memory.ServiceImpl;


import com.aliyun.oss.common.utils.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yesong.memory.memory.entity.User;
import com.yesong.memory.memory.mapper.UserMapper;
import com.yesong.memory.memory.request.UserRequest;
import com.yesong.memory.memory.response.CommonResponse;
import com.yesong.memory.memory.service.UserService;
import com.yesong.memory.memory.util.RedisKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public CommonResponse checkAccount(String account) {
        CommonResponse commonResponse = CommonResponse.builder().build();
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().eq(User::getAccount, account));
        if(users != null && users.size()>0){
            commonResponse.setSuccess(true);
        }else{
            commonResponse.setSuccess(false);
        }
        return commonResponse;
    }

    @Override
    @Transactional
    public CommonResponse regist(UserRequest userRequest) {
        User user = new User().getCreateEntity();
        BeanUtils.copyProperties(userRequest,user);
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        userMapper.insert(user);
        return CommonResponse.builder().success(true).build();
    }

    @Override
    public CommonResponse login(UserRequest userRequest) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().eq(User::getAccount, userRequest.getAccount()));
        if(users != null){
            User user = users.get(0);
            if(user.getPassword().equals(DigestUtils.md5Hex(userRequest.getPassword()))){
                String userToken = RedisKey.getUserToken(user.getAccount());
                String token = DigestUtils.md5Hex(System.currentTimeMillis()+user.getAccount()+user.getPassword());
                redisTemplate.opsForValue().set(userToken,token,90, TimeUnit.DAYS);
                return CommonResponse.builder().success(true).message("登录成功").body(token).build();
            }else{
                return CommonResponse.builder().success(false).message("账号密码错误").build();
            }
        }else{
            return CommonResponse.builder().success(false).message("用户不存在").build();
        }
    }
}
