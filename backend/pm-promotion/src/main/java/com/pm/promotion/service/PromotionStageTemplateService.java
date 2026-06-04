package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.ResultCode;
import com.pm.promotion.domain.PromotionStageTemplate;
import com.pm.promotion.dto.PromotionStageTemplateCreateRequest;
import com.pm.promotion.dto.PromotionStageTemplateUpdateRequest;
import com.pm.promotion.dto.PromotionStageTemplateVO;
import com.pm.promotion.mapper.PromotionStageTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionStageTemplateService {

    private final PromotionStageTemplateMapper templateMapper;

    @Transactional
    @RequirePermission("promotion:manage")
    public PromotionStageTemplateVO createTemplate(Long projectId, PromotionStageTemplateCreateRequest request) {
        PromotionStageTemplate template = new PromotionStageTemplate();
        BeanUtils.copyProperties(request, template);
        template.setProjectId(projectId);
        templateMapper.insert(template);
        return toVO(template);
    }

    public List<PromotionStageTemplateVO> listTemplates(Long projectId) {
        List<PromotionStageTemplate> templates = templateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .and(w -> w.isNull(PromotionStageTemplate::getProjectId)
                                .or()
                                .eq(PromotionStageTemplate::getProjectId, projectId))
                        .orderByAsc(PromotionStageTemplate::getSortOrder));
        return templates.stream().map(this::toVO).collect(Collectors.toList());
    }

    public PromotionStageTemplateVO getTemplate(Long templateId) {
        PromotionStageTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.STAGE_TEMPLATE_NOT_FOUND);
        }
        return toVO(template);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public PromotionStageTemplateVO updateTemplate(Long templateId, PromotionStageTemplateUpdateRequest request) {
        PromotionStageTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.STAGE_TEMPLATE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(template.getIsLocked())) {
            throw new BusinessException(ResultCode.STAGE_TEMPLATE_LOCKED);
        }
        BeanUtils.copyProperties(request, template, "id", "projectId", "createdAt", "createdBy");
        templateMapper.updateById(template);
        return toVO(template);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public void deleteTemplate(Long templateId) {
        PromotionStageTemplate template = templateMapper.selectById(templateId);
        if (template == null) {
            throw new BusinessException(ResultCode.STAGE_TEMPLATE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(template.getIsLocked())) {
            throw new BusinessException(ResultCode.STAGE_TEMPLATE_LOCKED);
        }
        templateMapper.deleteById(templateId);
    }

    /**
     * 初始化默认阶段模板（从全局模板复制到项目）
     */
    @Transactional
    @RequirePermission("promotion:manage")
    public List<PromotionStageTemplateVO> initDefaultTemplates(Long projectId) {
        // 检查项目是否已有模板
        Long count = templateMapper.selectCount(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .eq(PromotionStageTemplate::getProjectId, projectId));
        if (count > 0) {
            return listTemplates(projectId);
        }

        // 从全局模板复制
        List<PromotionStageTemplate> globalTemplates = templateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .isNull(PromotionStageTemplate::getProjectId)
                        .orderByAsc(PromotionStageTemplate::getSortOrder));

        for (PromotionStageTemplate global : globalTemplates) {
            PromotionStageTemplate copy = new PromotionStageTemplate();
            copy.setProjectId(projectId);
            copy.setName(global.getName());
            copy.setDescription(global.getDescription());
            copy.setSortOrder(global.getSortOrder());
            copy.setIsLocked(false);
            copy.setEstimatedDays(global.getEstimatedDays());
            templateMapper.insert(copy);
        }

        return listTemplates(projectId);
    }

    private PromotionStageTemplateVO toVO(PromotionStageTemplate template) {
        PromotionStageTemplateVO vo = new PromotionStageTemplateVO();
        BeanUtils.copyProperties(template, vo);
        return vo;
    }
}
