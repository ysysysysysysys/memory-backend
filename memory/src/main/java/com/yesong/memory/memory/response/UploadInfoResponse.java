package com.yesong.memory.memory.response;

import lombok.Data;

@Data
public class UploadInfoResponse {
    private String name;
    private String url;
    private Integer year;
    private String objectName;
    private Long expireTime;
    private String account;
    private String type;
    private String originalFilename;
    private Long total;
}
