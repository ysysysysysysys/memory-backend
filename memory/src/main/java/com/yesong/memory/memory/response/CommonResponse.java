package com.yesong.memory.memory.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResponse {
    private Boolean success;
    private String message;
    private Object body;

}
