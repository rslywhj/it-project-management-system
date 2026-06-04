package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.promotion.domain.ConfigBaseline;
import com.pm.promotion.domain.UnitConfigDiff;
import com.pm.promotion.dto.*;
import com.pm.promotion.mapper.ConfigBaselineMapper;
import com.pm.promotion.mapper.UnitConfigDiffMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigBaselineService {

    private final ConfigBaselineMapper baselineMapper;
    private final UnitConfigDiffMapper diffMapper;

    @Transactional
    @RequirePermission("promotion:manage")
    public ConfigBaselineVO createBaseline(Long projectId, ConfigBaselineCreateRequest request) {
        // 检查同项目下是否已存在相同key
        Long exists = baselineMapper.selectCount(
                new LambdaQueryWrapper<ConfigBaseline>()
                        .eq(ConfigBaseline::getProjectId, projectId)
                        .eq(ConfigBaseline::getConfigKey, request.getConfigKey()));
        if (exists > 0) {
            throw new BusinessException(ResultCode.CONFIG_BASELINE_KEY_EXISTS);
        }

        ConfigBaseline baseline = new ConfigBaseline();
        BeanUtils.copyProperties(request, baseline);
        baseline.setProjectId(projectId);
        baselineMapper.insert(baseline);
        return toVO(baseline);
    }

    public PageResult<ConfigBaselineVO> listBaselines(Long projectId, int page, int size, String keyword) {
        LambdaQueryWrapper<ConfigBaseline> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigBaseline::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), ConfigBaseline::getConfigKey, keyword)
                .orderByDesc(ConfigBaseline::getCreatedAt);

        Page<ConfigBaseline> result = baselineMapper.selectPage(new Page<>(page, size), wrapper);
        List<ConfigBaselineVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public ConfigBaselineVO getBaseline(Long baselineId) {
        ConfigBaseline baseline = baselineMapper.selectById(baselineId);
        if (baseline == null) {
            throw new BusinessException(ResultCode.CONFIG_BASELINE_NOT_FOUND);
        }
        return toVO(baseline);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public ConfigBaselineVO updateBaseline(Long baselineId, ConfigBaselineUpdateRequest request) {
        ConfigBaseline baseline = baselineMapper.selectById(baselineId);
        if (baseline == null) {
            throw new BusinessException(ResultCode.CONFIG_BASELINE_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, baseline, "id", "projectId", "configKey", "createdAt", "createdBy");
        baselineMapper.updateById(baseline);
        return toVO(baseline);
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public void deleteBaseline(Long baselineId) {
        ConfigBaseline baseline = baselineMapper.selectById(baselineId);
        if (baseline == null) {
            throw new BusinessException(ResultCode.CONFIG_BASELINE_NOT_FOUND);
        }
        baselineMapper.deleteById(baselineId);
    }

    // ===== 配置差异管理 =====

    @Transactional
    @RequirePermission("promotion:edit")
    public UnitConfigDiffVO createDiff(Long unitId, UnitConfigDiffCreateRequest request) {
        UnitConfigDiff diff = new UnitConfigDiff();
        BeanUtils.copyProperties(request, diff);
        diff.setPromotionUnitId(unitId);
        diff.setStatus("pending");
        diffMapper.insert(diff);
        return toDiffVO(diff);
    }

    public List<UnitConfigDiffVO> listDiffs(Long unitId) {
        List<UnitConfigDiff> diffs = diffMapper.selectList(
                new LambdaQueryWrapper<UnitConfigDiff>()
                        .eq(UnitConfigDiff::getPromotionUnitId, unitId)
                        .orderByDesc(UnitConfigDiff::getCreatedAt));
        return diffs.stream().map(this::toDiffVO).collect(Collectors.toList());
    }

    @Transactional
    @RequirePermission("promotion:manage")
    public UnitConfigDiffVO approveDiff(Long diffId, UnitConfigDiffApproveRequest request) {
        UnitConfigDiff diff = diffMapper.selectById(diffId);
        if (diff == null) {
            throw new BusinessException(ResultCode.UNIT_CONFIG_DIFF_NOT_FOUND);
        }
        if (!"pending".equals(diff.getStatus())) {
            throw new BusinessException(ResultCode.UNIT_CONFIG_DIFF_ALREADY_RESOLVED);
        }
        diff.setStatus(request.getStatus());
        diff.setApprovedBy(com.pm.common.util.UserContext.getUserId());
        diff.setApprovedAt(LocalDateTime.now());
        diffMapper.updateById(diff);
        return toDiffVO(diff);
    }

    private ConfigBaselineVO toVO(ConfigBaseline baseline) {
        ConfigBaselineVO vo = new ConfigBaselineVO();
        BeanUtils.copyProperties(baseline, vo);
        return vo;
    }

    private UnitConfigDiffVO toDiffVO(UnitConfigDiff diff) {
        UnitConfigDiffVO vo = new UnitConfigDiffVO();
        BeanUtils.copyProperties(diff, vo);

        // 填充基线信息
        ConfigBaseline baseline = baselineMapper.selectById(diff.getConfigBaselineId());
        if (baseline != null) {
            vo.setConfigKey(baseline.getConfigKey());
            vo.setBaselineValue(baseline.getConfigValue());
        }
        return vo;
    }
}
