package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.promotion.domain.UnitRequirement;
import com.pm.promotion.dto.UnitRequirementCreateRequest;
import com.pm.promotion.dto.UnitRequirementUpdateRequest;
import com.pm.promotion.dto.UnitRequirementVO;
import com.pm.promotion.mapper.UnitRequirementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnitRequirementService {

    private final UnitRequirementMapper requirementMapper;

    @Transactional
    @RequirePermission("promotion:edit")
    public UnitRequirementVO createRequirement(Long unitId, UnitRequirementCreateRequest request) {
        UnitRequirement req = new UnitRequirement();
        BeanUtils.copyProperties(request, req);
        req.setPromotionUnitId(unitId);
        if (req.getType() == null) {
            req.setType("differential");
        }
        if (req.getPriority() == null) {
            req.setPriority("medium");
        }
        req.setStatus("draft");
        requirementMapper.insert(req);
        return toVO(req);
    }

    public PageResult<UnitRequirementVO> listRequirements(Long unitId, int page, int size,
                                                            String keyword, String type) {
        LambdaQueryWrapper<UnitRequirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UnitRequirement::getPromotionUnitId, unitId)
                .like(StringUtils.hasText(keyword), UnitRequirement::getTitle, keyword)
                .eq(StringUtils.hasText(type), UnitRequirement::getType, type)
                .orderByDesc(UnitRequirement::getCreatedAt);

        Page<UnitRequirement> result = requirementMapper.selectPage(new Page<>(page, size), wrapper);
        List<UnitRequirementVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public UnitRequirementVO getRequirement(Long requirementId) {
        UnitRequirement req = requirementMapper.selectById(requirementId);
        if (req == null) {
            throw new BusinessException(ResultCode.UNIT_REQUIREMENT_NOT_FOUND);
        }
        return toVO(req);
    }

    @Transactional
    @RequirePermission("promotion:edit")
    public UnitRequirementVO updateRequirement(Long requirementId, UnitRequirementUpdateRequest request) {
        UnitRequirement req = requirementMapper.selectById(requirementId);
        if (req == null) {
            throw new BusinessException(ResultCode.UNIT_REQUIREMENT_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, req, "id", "promotionUnitId", "requirementId", "createdAt", "createdBy");
        requirementMapper.updateById(req);
        return toVO(req);
    }

    @Transactional
    @RequirePermission("promotion:edit")
    public void deleteRequirement(Long requirementId) {
        UnitRequirement req = requirementMapper.selectById(requirementId);
        if (req == null) {
            throw new BusinessException(ResultCode.UNIT_REQUIREMENT_NOT_FOUND);
        }
        requirementMapper.deleteById(requirementId);
    }

    private UnitRequirementVO toVO(UnitRequirement req) {
        UnitRequirementVO vo = new UnitRequirementVO();
        BeanUtils.copyProperties(req, vo);
        return vo;
    }
}
