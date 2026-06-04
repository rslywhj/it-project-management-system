package com.pm.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.resource.domain.WorkLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkLogMapper extends BaseMapper<WorkLog> {
}
