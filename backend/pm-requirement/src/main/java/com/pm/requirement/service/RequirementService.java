package com.pm.requirement.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.requirement.domain.Requirement;
import com.pm.requirement.dto.RequirementCreateRequest;
import com.pm.requirement.dto.RequirementVO;
import com.pm.requirement.dto.StatusUpdateRequest;
import com.pm.requirement.enums.RequirementStatus;
import com.pm.requirement.mapper.RequirementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementMapper requirementMapper;

    @Transactional
    @RequirePermission("requirement:create")
    public RequirementVO createRequirement(Long projectId, RequirementCreateRequest request) {
        Requirement requirement = new Requirement();
        BeanUtils.copyProperties(request, requirement);
        requirement.setProjectId(projectId);
        requirement.setStatus(RequirementStatus.DRAFT.getCode());
        requirementMapper.insert(requirement);
        return toVO(requirement);
    }

    public PageResult<RequirementVO> listRequirements(Long projectId, int page, int size,
                                                       String keyword, String status, String priority) {
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Requirement::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), Requirement::getTitle, keyword)
                .eq(StringUtils.hasText(status), Requirement::getStatus, status)
                .eq(StringUtils.hasText(priority), Requirement::getPriority, priority)
                .orderByDesc(Requirement::getCreatedAt);

        Page<Requirement> result = requirementMapper.selectPage(new Page<>(page, size), wrapper);
        List<RequirementVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public RequirementVO getRequirement(Long requirementId) {
        Requirement requirement = requirementMapper.selectById(requirementId);
        if (requirement == null) {
            throw new BusinessException(ResultCode.REQUIREMENT_NOT_FOUND);
        }
        return toVO(requirement);
    }

    @Transactional
    @RequirePermission("requirement:update")
    public RequirementVO updateRequirement(Long requirementId, RequirementCreateRequest request) {
        Requirement requirement = requirementMapper.selectById(requirementId);
        if (requirement == null) {
            throw new BusinessException(ResultCode.REQUIREMENT_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, requirement, "id", "projectId", "status", "createdAt", "createdBy");
        requirementMapper.updateById(requirement);
        return toVO(requirement);
    }

    @Transactional
    @RequirePermission("requirement:update")
    public RequirementVO updateStatus(Long requirementId, StatusUpdateRequest request) {
        Requirement requirement = requirementMapper.selectById(requirementId);
        if (requirement == null) {
            throw new BusinessException(ResultCode.REQUIREMENT_NOT_FOUND);
        }

        String currentStatus = requirement.getStatus();
        String targetStatus = request.getTargetStatus();

        if (!RequirementStatus.isValidTransition(currentStatus, targetStatus)) {
            throw new BusinessException(ResultCode.REQUIREMENT_STATUS_INVALID,
                    String.format("不允许从 [%s] 流转到 [%s]", currentStatus, targetStatus));
        }

        requirement.setStatus(targetStatus);
        requirementMapper.updateById(requirement);
        return toVO(requirement);
    }

    @Transactional
    @RequirePermission("requirement:delete")
    public void deleteRequirement(Long requirementId) {
        requirementMapper.deleteById(requirementId);
    }

    /**
     * 需求池：获取未排期的需求
     */
    public List<RequirementVO> getRequirementPool(Long projectId) {
        LambdaQueryWrapper<Requirement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Requirement::getProjectId, projectId)
                .in(Requirement::getStatus, RequirementStatus.DRAFT.getCode(),
                        RequirementStatus.APPROVED.getCode())
                .isNull(Requirement::getMilestoneId)
                .orderByAsc(Requirement::getPriority);

        return requirementMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Transactional
    @RequirePermission("requirement:update")
    public void assignRequirement(Long requirementId, Long userId) {
        Requirement requirement = requirementMapper.selectById(requirementId);
        if (requirement == null) {
            throw new BusinessException(ResultCode.REQUIREMENT_NOT_FOUND);
        }
        requirement.setAssignedTo(userId);
        requirementMapper.updateById(requirement);
    }

    private RequirementVO toVO(Requirement requirement) {
        RequirementVO vo = new RequirementVO();
        BeanUtils.copyProperties(requirement, vo);
        return vo;
    }
}
