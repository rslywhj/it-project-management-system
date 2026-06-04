package com.pm.resource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.resource.domain.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {
}
