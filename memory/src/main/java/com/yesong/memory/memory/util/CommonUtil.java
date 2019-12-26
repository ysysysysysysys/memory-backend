package com.yesong.memory.memory.util;

public class CommonUtil {
    public static String getName(String account,String type,String name){
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append(account).append("/").append(type).append(name).toString();
    }
}
