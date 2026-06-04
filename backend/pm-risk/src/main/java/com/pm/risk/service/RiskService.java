package com.pm.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.risk.domain.Risk;
import com.pm.risk.dto.*;
import com.pm.risk.enums.RiskStatus;
import com.pm.risk.mapper.RiskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final RiskMapper riskMapper;

    private static final ResultCode RISK_NOT_FOUND = ResultCode.RISK_NOT_FOUND;
    private static final ResultCode RISK_STATUS_INVALID = ResultCode.RISK_STATUS_INVALID;

    @Transactional
    @RequirePermission("risk:create")
    public RiskVO createRisk(Long projectId, RiskCreateRequest request) {
        Risk risk = new Risk();
        BeanUtils.copyProperties(request, risk);
        risk.setProjectId(projectId);
        risk.setStatus(RiskStatus.IDENTIFIED.getCode());
        if (risk.getIdentifiedDate() == null) {
            risk.setIdentifiedDate(LocalDate.now());
        }

        riskMapper.insert(risk);
        return toVO(risk);
    }

    public PageResult<RiskVO> listRisks(Long projectId, int page, int size,
                                         String keyword, String status, String level, Long ownerId) {
        LambdaQueryWrapper<Risk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Risk::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), Risk::getTitle, keyword)
                .eq(StringUtils.hasText(status), Risk::getStatus, status)
                .eq(StringUtils.hasText(level), Risk::getLevel, level)
                .eq(ownerId != null, Risk::getOwnerId, ownerId)
                .orderByDesc(Risk::getCreatedAt);

        Page<Risk> result = riskMapper.selectPage(new Page<>(page, size), wrapper);
        List<RiskVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public RiskVO getRisk(Long riskId) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(RISK_NOT_FOUND);
        }
        return toVO(risk);
    }

    @Transactional
    @RequirePermission("risk:update")
    public RiskVO updateRisk(Long riskId, RiskUpdateRequest request) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(RISK_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, risk, "id", "projectId", "status", "createdAt", "createdBy");
        riskMapper.updateById(risk);
        return toVO(risk);
    }

    @Transactional
    @RequirePermission("risk:update")
    public RiskVO updateRiskStatus(Long riskId, RiskStatusUpdateRequest request) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(RISK_NOT_FOUND);
        }

        String currentStatus = risk.getStatus();
        String targetStatus = request.getStatus();

        if (!RiskStatus.isValidTransition(currentStatus, targetStatus)) {
            throw new BusinessException(RISK_STATUS_INVALID);
        }

        risk.setStatus(targetStatus);

        // 如果关闭风险，记录实际解决日期
        if (RiskStatus.CLOSED.getCode().equals(targetStatus)) {
            risk.setActualResolutionDate(LocalDate.now());
        }

        riskMapper.updateById(risk);
        return toVO(risk);
    }

    @Transactional
    @RequirePermission("risk:delete")
    public void deleteRisk(Long riskId) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(RISK_NOT_FOUND);
        }
        riskMapper.deleteById(riskId);
    }

    /**
     * 获取即将到期的风险列表
     */
    public List<RiskVO> getExpiringRisks(Long projectId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        LambdaQueryWrapper<Risk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Risk::getProjectId, projectId)
                .ne(Risk::getStatus, RiskStatus.CLOSED.getCode())
                .isNotNull(Risk::getTargetResolutionDate)
                .le(Risk::getTargetResolutionDate, thresholdDate)
                .ge(Risk::getTargetResolutionDate, LocalDate.now())
                .orderByAsc(Risk::getTargetResolutionDate);

        return riskMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取已逾期的风险列表
     */
    public List<RiskVO> getOverdueRisks(Long projectId) {
        LambdaQueryWrapper<Risk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Risk::getProjectId, projectId)
                .ne(Risk::getStatus, RiskStatus.CLOSED.getCode())
                .isNotNull(Risk::getTargetResolutionDate)
                .lt(Risk::getTargetResolutionDate, LocalDate.now())
                .orderByAsc(Risk::getTargetResolutionDate);

        return riskMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private RiskVO toVO(Risk risk) {
        RiskVO vo = new RiskVO();
        BeanUtils.copyProperties(risk, vo);
        return vo;
    }
}
