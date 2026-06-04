package com.pm.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.test.domain.TestCase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestCaseMapper extends BaseMapper<TestCase> {
}
