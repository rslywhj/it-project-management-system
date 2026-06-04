package com.pm.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.project.domain.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
}
