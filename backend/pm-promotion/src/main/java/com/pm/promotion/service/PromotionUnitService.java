package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.promotion.domain.PromotionProgress;
import com.pm.promotion.domain.PromotionStageTemplate;
import com.pm.promotion.domain.PromotionUnit;
import com.pm.promotion.dto.*;
import com.pm.promotion.mapper.PromotionProgressMapper;
import com.pm.promotion.mapper.PromotionStageTemplateMapper;
import com.pm.promotion.mapper.PromotionUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionUnitService {

    private final PromotionUnitMapper unitMapper;
    private final PromotionStageTemplateMapper stageTemplateMapper;
    private final PromotionProgressMapper progressMapper;

    @Transactional
    @RequirePermission("promotion:create")
    public PromotionUnitVO createUnit(Long projectId, PromotionUnitCreateRequest request) {
        Long exists = unitMapper.selectCount(
                new LambdaQueryWrapper<PromotionUnit>()
                        .eq(PromotionUnit::getProjectId, projectId)
                        .eq(PromotionUnit::getOrgCode, request.getOrgCode()));
        if (exists > 0) {
            throw new BusinessException(ResultCode.PROMOTION_UNIT_ORG_EXISTS);
        }

        PromotionUnit unit = new PromotionUnit();
        BeanUtils.copyProperties(request, unit);
        unit.setProjectId(projectId);
        unit.setStatus("pending");
        unit.setCompletionRate(BigDecimal.ZERO);
        unitMapper.insert(unit);

        initProgressForUnit(unit.getId(), projectId);

        return toVO(unit);
    }

    @Transactional
    @RequirePermission("promotion:create")
    public List<PromotionUnitVO> batchCreateUnits(Long projectId, List<PromotionUnitCreateRequest> requests) {
        List<PromotionUnitVO> result = new ArrayList<>();
        for (PromotionUnitCreateRequest request : requests) {
            result.add(createUnit(projectId, request));
        }
        return result;
    }

    public PageResult<PromotionUnitVO> listUnits(Long projectId, int page, int size,
                                                   String keyword, String status) {
        LambdaQueryWrapper<PromotionUnit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PromotionUnit::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), PromotionUnit::getOrgName, keyword)
                .eq(StringUtils.hasText(status), PromotionUnit::getStatus, status)
                .orderByDesc(PromotionUnit::getCreatedAt);

        Page<PromotionUnit> result = unitMapper.selectPage(new Page<>(page, size), wrapper);
        List<PromotionUnitVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public PromotionUnitVO getUnit(Long unitId) {
        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(ResultCode.PROMOTION_UNIT_NOT_FOUND);
        }
        return toVO(unit);
    }

    @Transactional
    @RequirePermission("promotion:edit")
    public PromotionUnitVO updateUnit(Long unitId, PromotionUnitUpdateRequest request) {
        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(ResultCode.PROMOTION_UNIT_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, unit, "id", "projectId", "orgCode", "status",
                "currentStageId", "completionRate", "createdAt", "createdBy");
        unitMapper.updateById(unit);
        return getUnit(unitId);
    }

    @Transactional
    @RequirePermission("promotion:delete")
    public void deleteUnit(Long unitId) {
        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit == null) {
            throw new BusinessException(ResultCode.PROMOTION_UNIT_NOT_FOUND);
        }
        progressMapper.delete(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId));
        unitMapper.deleteById(unitId);
    }

    private void initProgressForUnit(Long unitId, Long projectId) {
        List<PromotionStageTemplate> templates = stageTemplateMapper.selectList(
                new LambdaQueryWrapper<PromotionStageTemplate>()
                        .and(w -> w.isNull(PromotionStageTemplate::getProjectId)
                                .or()
                                .eq(PromotionStageTemplate::getProjectId, projectId))
                        .orderByAsc(PromotionStageTemplate::getSortOrder));

        for (PromotionStageTemplate template : templates) {
            PromotionProgress progress = new PromotionProgress();
            progress.setPromotionUnitId(unitId);
            progress.setStageTemplateId(template.getId());
            progress.setStatus("pending");
            progress.setCompletionRate(BigDecimal.ZERO);
            progressMapper.insert(progress);
        }
    }

    private PromotionUnitVO toVO(PromotionUnit unit) {
        PromotionUnitVO vo = new PromotionUnitVO();
        BeanUtils.copyProperties(unit, vo);

        if (unit.getCurrentStageId() != null) {
            PromotionStageTemplate stage = stageTemplateMapper.selectById(unit.getCurrentStageId());
            if (stage != null) {
                vo.setCurrentStageName(stage.getName());
            }
        }

        List<PromotionProgress> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unit.getId())
                        .orderByAsc(PromotionProgress::getId));

        if (!progressList.isEmpty()) {
            List<Long> templateIds = progressList.stream()
                    .map(PromotionProgress::getStageTemplateId)
                    .collect(Collectors.toList());
            Map<Long, PromotionStageTemplate> templateMap = stageTemplateMapper.selectBatchIds(templateIds)
                    .stream()
                    .collect(Collectors.toMap(PromotionStageTemplate::getId, t -> t));

            List<PromotionProgressVO> progressVOs = progressList.stream()
                    .map(p -> {
                        PromotionProgressVO pvo = new PromotionProgressVO();
                        BeanUtils.copyProperties(p, pvo);
                        PromotionStageTemplate tpl = templateMap.get(p.getStageTemplateId());
                        if (tpl != null) {
                            pvo.setStageTemplateName(tpl.getName());
                            pvo.setSortOrder(tpl.getSortOrder());
                            pvo.setIsLocked(tpl.getIsLocked());
                        }
                        return pvo;
                    })
                    .collect(Collectors.toList());
            vo.setProgressList(progressVOs);
        }

        return vo;
    }
}
