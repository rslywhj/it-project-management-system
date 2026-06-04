package com.pm.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.task.domain.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
