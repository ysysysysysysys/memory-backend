package com.yesong.memory.memory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class BaseEntity<T> implements Serializable {
    @TableId(type = IdType.UUID)
    private String id;
    private Date createTime;
    private Date updateTime = new Date();
    @Version
    private Integer version;
    @TableLogic
    private Integer deleted;

    public <T> T getCreateEntity(){
        this.setCreateTime(new Date());
        this.setVersion(1);
        this.setDeleted(0);
        return (T) this;
    }

    public <T> T getUpdateEntity(){
        this.setUpdateTime(new Date());
        return (T)this;
    }

}
