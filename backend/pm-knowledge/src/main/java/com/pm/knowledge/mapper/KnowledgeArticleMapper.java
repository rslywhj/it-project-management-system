package com.pm.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pm.knowledge.domain.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {
}
