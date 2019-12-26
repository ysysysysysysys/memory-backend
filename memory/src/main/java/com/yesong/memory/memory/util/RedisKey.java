package com.yesong.memory.memory.util;

public class RedisKey {
    public static String getUserToken(String userName){
        return "token:"+userName;
    }
}
