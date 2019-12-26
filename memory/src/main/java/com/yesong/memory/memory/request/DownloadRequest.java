package com.yesong.memory.memory.request;

import com.yesong.memory.memory.enums.MemoryType;
import lombok.Data;

@Data
public class DownloadRequest {
    private String objectName;
    private String account;
    private MemoryType memoryType;
}
