package com.yesong.memory.memory.handler;


import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yesong.memory.memory.annotations.Auth;
import com.yesong.memory.memory.util.RedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class MemoryHandler implements HandlerInterceptor {
    private final static Logger log = LoggerFactory.getLogger(MemoryHandler.class);
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Boolean state = this.checkAnnoState(handler);
        if(! state){
            //如果没有注解直接放行
            return true;
        }else{
            String token = request.getHeader("token");
            String account = request.getHeader("account");
            Map<String, String> map = new HashMap<>();
            map.put("state","false");
            map.put("success","401");
            if(StringUtils.isEmpty(token) || StringUtils.isEmpty(account)){
                backMessage(response,JSONObject.toJSONString(map));
                return false;
            }
            String userToken = RedisKey.getUserToken((String) account);
            Object o = redisTemplate.opsForValue().get(userToken);
            //先从缓存里拿,如果有对比 如果相同 放行
            if(o != null && token.equals(o.toString())){
                return true;
            }else{
                backMessage(response,JSONObject.toJSONString(map));
                return false;
            }
        }
    }

    public Boolean checkAnnoState(Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //先看被拦截的这个方法上是否有校验用户注解
            Auth methodAnno = handlerMethod.getMethod().getAnnotation(Auth.class);
            //如果没有看这个类上有没有
            Auth typeAnno = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Auth.class);
            if (methodAnno == null && typeAnno == null) {
                return false;
            }else{
                return true;
            }
        }
        return true;
    }

    public void backMessage(HttpServletResponse response,String json){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter();){
            writer.print(json);
        }catch (IOException e){
            log.info("写出有误{}",e);
        }
    }
}
