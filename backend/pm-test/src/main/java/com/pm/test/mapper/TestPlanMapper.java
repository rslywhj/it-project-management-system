package com.pm.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.test.domain.TestPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestPlanMapper extends BaseMapper<TestPlan> {
}
