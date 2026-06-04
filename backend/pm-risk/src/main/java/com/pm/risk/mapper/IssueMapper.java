package com.pm.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.risk.domain.Issue;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IssueMapper extends BaseMapper<Issue> {
}
