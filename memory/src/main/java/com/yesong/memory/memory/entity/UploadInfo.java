package com.yesong.memory.memory.entity;

import lombok.Data;

@Data
public class UploadInfo extends BaseEntity<UploadInfo>{
    private Integer year;
    private String objectName;
    private Long expireTime;
    private String account;
    private String type;
    private String originalFilename;
    private String url;
}
