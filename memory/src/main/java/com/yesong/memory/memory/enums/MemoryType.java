package com.yesong.memory.memory.enums;

import java.util.Calendar;

public enum MemoryType {
    VIDEO("video","video/"),
    IMAGE("image","image/"),
    FILE("file","file/")
    ;
    private String key;
    private String path;

    MemoryType(String key, String path) {
        this.key = key;
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPath() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.YEAR);
        return i+"/"+path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
