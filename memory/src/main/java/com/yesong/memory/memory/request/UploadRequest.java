package com.yesong.memory.memory.request;

import com.yesong.memory.memory.enums.MemoryType;
import lombok.Data;

@Data
public class UploadRequest {
    private String account;
    private MemoryType memoryType;
    private Integer year;
    private Integer pageNum;
    private Integer pageSize;
}
