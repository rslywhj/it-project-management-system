package com.pm.milestone.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.milestone.domain.Milestone;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MilestoneMapper extends BaseMapper<Milestone> {
}
