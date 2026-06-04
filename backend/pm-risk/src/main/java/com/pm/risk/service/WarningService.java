package com.pm.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.risk.domain.Issue;
import com.pm.risk.domain.Risk;
import com.pm.risk.domain.RiskWarning;
import com.pm.risk.dto.WarningVO;
import com.pm.risk.enums.IssueStatus;
import com.pm.risk.enums.RiskStatus;
import com.pm.risk.enums.WarningStatus;
import com.pm.risk.enums.WarningType;
import com.pm.risk.mapper.IssueMapper;
import com.pm.risk.mapper.RiskMapper;
import com.pm.risk.mapper.RiskWarningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarningService {

    private final RiskWarningMapper warningMapper;
    private final RiskMapper riskMapper;
    private final IssueMapper issueMapper;

    private static final ResultCode WARNING_NOT_FOUND = ResultCode.WARNING_NOT_FOUND;

    /**
     * 检查并生成预警
     */
    @Transactional
    public List<WarningVO> checkAndGenerateWarnings(Long projectId, int daysThreshold) {
        List<WarningVO> warnings = new ArrayList<>();

        // 检查风险到期预警
        warnings.addAll(checkRiskExpiryWarnings(projectId, daysThreshold));

        // 检查问题到期预警
        warnings.addAll(checkIssueExpiryWarnings(projectId, daysThreshold));

        return warnings;
    }

    /**
     * 检查风险到期预警
     */
    private List<WarningVO> checkRiskExpiryWarnings(Long projectId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        LambdaQueryWrapper<Risk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Risk::getProjectId, projectId)
                .ne(Risk::getStatus, RiskStatus.CLOSED.getCode())
                .isNotNull(Risk::getTargetResolutionDate)
                .le(Risk::getTargetResolutionDate, thresholdDate)
                .ge(Risk::getTargetResolutionDate, LocalDate.now());

        List<Risk> expiringRisks = riskMapper.selectList(wrapper);
        List<WarningVO> warnings = new ArrayList<>();

        for (Risk risk : expiringRisks) {
            // 检查是否已存在未处理的预警
            if (!hasExistingWarning(risk.getId(), "risk")) {
                RiskWarning warning = new RiskWarning();
                warning.setProjectId(projectId);
                warning.setWarningType(WarningType.RISK_EXPIRY.getCode());
                warning.setRelatedId(risk.getId());
                warning.setRelatedType("risk");
                warning.setWarningMessage(String.format("风险「%s」将于 %s 到期，请及时处理",
                        risk.getTitle(), risk.getTargetResolutionDate()));
                warning.setWarningDate(LocalDateTime.now());
                warning.setStatus(WarningStatus.PENDING.getCode());

                warningMapper.insert(warning);
                warnings.add(toVO(warning));
            }
        }

        return warnings;
    }

    /**
     * 检查问题到期预警
     */
    private List<WarningVO> checkIssueExpiryWarnings(Long projectId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, projectId)
                .ne(Issue::getStatus, IssueStatus.CLOSED.getCode())
                .ne(Issue::getStatus, IssueStatus.RESOLVED.getCode())
                .isNotNull(Issue::getTargetResolutionDate)
                .le(Issue::getTargetResolutionDate, thresholdDate)
                .ge(Issue::getTargetResolutionDate, LocalDate.now());

        List<Issue> expiringIssues = issueMapper.selectList(wrapper);
        List<WarningVO> warnings = new ArrayList<>();

        for (Issue issue : expiringIssues) {
            // 检查是否已存在未处理的预警
            if (!hasExistingWarning(issue.getId(), "issue")) {
                RiskWarning warning = new RiskWarning();
                warning.setProjectId(projectId);
                warning.setWarningType(WarningType.ISSUE_EXPIRY.getCode());
                warning.setRelatedId(issue.getId());
                warning.setRelatedType("issue");
                warning.setWarningMessage(String.format("问题「%s」将于 %s 到期，请及时处理",
                        issue.getTitle(), issue.getTargetResolutionDate()));
                warning.setWarningDate(LocalDateTime.now());
                warning.setStatus(WarningStatus.PENDING.getCode());

                warningMapper.insert(warning);
                warnings.add(toVO(warning));
            }
        }

        return warnings;
    }

    /**
     * 检查是否已存在未处理的预警
     */
    private boolean hasExistingWarning(Long relatedId, String relatedType) {
        LambdaQueryWrapper<RiskWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskWarning::getRelatedId, relatedId)
                .eq(RiskWarning::getRelatedType, relatedType)
                .eq(RiskWarning::getStatus, WarningStatus.PENDING.getCode());
        return warningMapper.selectCount(wrapper) > 0;
    }

    /**
     * 获取预警列表
     */
    public PageResult<WarningVO> listWarnings(Long projectId, int page, int size,
                                               String warningType, String status) {
        LambdaQueryWrapper<RiskWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskWarning::getProjectId, projectId)
                .eq(warningType != null, RiskWarning::getWarningType, warningType)
                .eq(status != null, RiskWarning::getStatus, status)
                .orderByDesc(RiskWarning::getWarningDate);

        Page<RiskWarning> result = warningMapper.selectPage(new Page<>(page, size), wrapper);
        List<WarningVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    /**
     * 获取待处理预警列表
     */
    public List<WarningVO> getPendingWarnings(Long projectId) {
        LambdaQueryWrapper<RiskWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskWarning::getProjectId, projectId)
                .eq(RiskWarning::getStatus, WarningStatus.PENDING.getCode())
                .orderByDesc(RiskWarning::getWarningDate);

        return warningMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 确认预警
     */
    @Transactional
    @RequirePermission("risk:update")
    public WarningVO acknowledgeWarning(Long warningId) {
        RiskWarning warning = warningMapper.selectById(warningId);
        if (warning == null) {
            throw new BusinessException(WARNING_NOT_FOUND);
        }

        warning.setStatus(WarningStatus.ACKNOWLEDGED.getCode());
        warning.setAcknowledgedBy(UserContext.getUserId());
        warning.setAcknowledgedAt(LocalDateTime.now());

        warningMapper.updateById(warning);
        return toVO(warning);
    }

    /**
     * 忽略预警
     */
    @Transactional
    @RequirePermission("risk:update")
    public WarningVO dismissWarning(Long warningId) {
        RiskWarning warning = warningMapper.selectById(warningId);
        if (warning == null) {
            throw new BusinessException(WARNING_NOT_FOUND);
        }

        warning.setStatus(WarningStatus.DISMISSED.getCode());
        warning.setAcknowledgedBy(UserContext.getUserId());
        warning.setAcknowledgedAt(LocalDateTime.now());

        warningMapper.updateById(warning);
        return toVO(warning);
    }

    /**
     * 获取项目预警统计
     */
    public long countPendingWarnings(Long projectId) {
        LambdaQueryWrapper<RiskWarning> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RiskWarning::getProjectId, projectId)
                .eq(RiskWarning::getStatus, WarningStatus.PENDING.getCode());
        return warningMapper.selectCount(wrapper);
    }

    private WarningVO toVO(RiskWarning warning) {
        WarningVO vo = new WarningVO();
        BeanUtils.copyProperties(warning, vo);
        return vo;
    }
}
