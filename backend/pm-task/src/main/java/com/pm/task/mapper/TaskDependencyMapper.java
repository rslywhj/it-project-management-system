package com.pm.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.task.domain.TaskDependency;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskDependencyMapper extends BaseMapper<TaskDependency> {
}
