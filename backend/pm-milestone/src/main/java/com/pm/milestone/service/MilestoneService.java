package com.pm.milestone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.milestone.domain.Milestone;
import com.pm.milestone.dto.MilestoneCreateRequest;
import com.pm.milestone.dto.MilestoneUpdateRequest;
import com.pm.milestone.dto.MilestoneVO;
import com.pm.milestone.mapper.MilestoneMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MilestoneService {

    private final MilestoneMapper milestoneMapper;

    @Transactional
    @RequirePermission("milestone:create")
    public MilestoneVO createMilestone(Long projectId, MilestoneCreateRequest request) {
        Milestone milestone = new Milestone();
        BeanUtils.copyProperties(request, milestone);
        milestone.setProjectId(projectId);
        milestone.setStatus("pending");
        milestoneMapper.insert(milestone);
        return toVO(milestone);
    }

    public PageResult<MilestoneVO> listMilestones(Long projectId, int page, int size, String status) {
        LambdaQueryWrapper<Milestone> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Milestone::getProjectId, projectId)
                .eq(StringUtils.hasText(status), Milestone::getStatus, status)
                .orderByAsc(Milestone::getPlannedDate);

        Page<Milestone> result = milestoneMapper.selectPage(new Page<>(page, size), wrapper);
        List<MilestoneVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public MilestoneVO getMilestone(Long milestoneId) {
        Milestone milestone = milestoneMapper.selectById(milestoneId);
        if (milestone == null) {
            throw new BusinessException(ResultCode.MILESTONE_NOT_FOUND);
        }
        return toVO(milestone);
    }

    @Transactional
    @RequirePermission("milestone:edit")
    public MilestoneVO updateMilestone(Long milestoneId, MilestoneUpdateRequest request) {
        Milestone milestone = milestoneMapper.selectById(milestoneId);
        if (milestone == null) {
            throw new BusinessException(ResultCode.MILESTONE_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, milestone, "id", "projectId", "createdAt", "createdBy", "isDeleted");
        milestoneMapper.updateById(milestone);
        return toVO(milestone);
    }

    @Transactional
    @RequirePermission("milestone:delete")
    public void deleteMilestone(Long milestoneId) {
        Milestone milestone = milestoneMapper.selectById(milestoneId);
        if (milestone == null) {
            throw new BusinessException(ResultCode.MILESTONE_NOT_FOUND);
        }
        milestoneMapper.deleteById(milestoneId);
    }

    private MilestoneVO toVO(Milestone milestone) {
        MilestoneVO vo = new MilestoneVO();
        BeanUtils.copyProperties(milestone, vo);
        return vo;
    }
}
