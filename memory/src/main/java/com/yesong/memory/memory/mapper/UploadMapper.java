package com.yesong.memory.memory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yesong.memory.memory.entity.UploadInfo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface UploadMapper extends BaseMapper<UploadInfo> {
}
