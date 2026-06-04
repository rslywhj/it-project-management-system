package com.pm.requirement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.requirement.domain.Requirement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RequirementMapper extends BaseMapper<Requirement> {
}
