package com.pm.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.util.UserContext;
import com.pm.risk.domain.Issue;
import com.pm.risk.domain.Risk;
import com.pm.risk.dto.*;
import com.pm.risk.mapper.IssueMapper;
import com.pm.risk.mapper.RiskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiskService {

    private final RiskMapper riskMapper;
    private final IssueMapper issueMapper;

    // ==================== 风险管理 ====================

    @Transactional
    @RequirePermission("risk:create")
    public RiskVO createRisk(Long projectId, RiskRequest request) {
        Risk risk = new Risk();
        BeanUtils.copyProperties(request, risk);
        risk.setProjectId(projectId);
        risk.setStatus("open");
        risk.setIdentifiedDate(request.getIdentifiedDate() != null ? request.getIdentifiedDate() : LocalDate.now());

        // 自动计算风险等级
        risk.setRiskLevel(calculateRiskLevel(request.getProbability(), request.getImpact()));

        riskMapper.insert(risk);
        return toRiskVO(risk);
    }

    public PageResult<RiskVO> listRisks(Long projectId, int page, int size, String status, String riskLevel) {
        LambdaQueryWrapper<Risk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Risk::getProjectId, projectId)
                .eq(StringUtils.hasText(status), Risk::getStatus, status)
                .eq(StringUtils.hasText(riskLevel), Risk::getRiskLevel, riskLevel)
                .orderByDesc(Risk::getCreatedAt);

        Page<Risk> result = riskMapper.selectPage(new Page<>(page, size), wrapper);
        List<RiskVO> voList = result.getRecords().stream()
                .map(this::toRiskVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public RiskVO getRisk(Long riskId) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(1090, "风险不存在");
        }
        return toRiskVO(risk);
    }

    @Transactional
    @RequirePermission("risk:edit")
    public RiskVO updateRisk(Long riskId, RiskRequest request) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(1090, "风险不存在");
        }
        BeanUtils.copyProperties(request, risk, "id", "projectId", "status", "closedDate",
                "createdAt", "createdBy", "isDeleted");
        risk.setRiskLevel(calculateRiskLevel(request.getProbability(), request.getImpact()));
        riskMapper.updateById(risk);
        return toRiskVO(risk);
    }

    @Transactional
    @RequirePermission("risk:edit")
    public RiskVO updateRiskStatus(Long riskId, String status) {
        Risk risk = riskMapper.selectById(riskId);
        if (risk == null) {
            throw new BusinessException(1090, "风险不存在");
        }
        risk.setStatus(status);
        if ("closed".equals(status)) {
            risk.setClosedDate(LocalDate.now());
        }
        riskMapper.updateById(risk);
        return toRiskVO(risk);
    }

    @Transactional
    @RequirePermission("risk:delete")
    public void deleteRisk(Long riskId) {
        riskMapper.deleteById(riskId);
    }

    // ==================== 问题管理 ====================

    @Transactional
    @RequirePermission("risk:create")
    public IssueVO createIssue(Long projectId, IssueRequest request) {
        Issue issue = new Issue();
        BeanUtils.copyProperties(request, issue);
        issue.setProjectId(projectId);
        issue.setStatus("open");
        issue.setReportedBy(UserContext.getUserId());
        issueMapper.insert(issue);
        return toIssueVO(issue);
    }

    public PageResult<IssueVO> listIssues(Long projectId, int page, int size, String status, String severity) {
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, projectId)
                .eq(StringUtils.hasText(status), Issue::getStatus, status)
                .eq(StringUtils.hasText(severity), Issue::getSeverity, severity)
                .orderByDesc(Issue::getCreatedAt);

        Page<Issue> result = issueMapper.selectPage(new Page<>(page, size), wrapper);
        List<IssueVO> voList = result.getRecords().stream()
                .map(this::toIssueVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public IssueVO getIssue(Long issueId) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(1091, "问题不存在");
        }
        return toIssueVO(issue);
    }

    @Transactional
    @RequirePermission("risk:edit")
    public IssueVO updateIssue(Long issueId, IssueRequest request) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(1091, "问题不存在");
        }
        BeanUtils.copyProperties(request, issue, "id", "projectId", "status", "reportedBy",
                "resolution", "resolvedAt", "resolvedBy", "closedAt",
                "createdAt", "createdBy", "isDeleted");
        issueMapper.updateById(issue);
        return toIssueVO(issue);
    }

    @Transactional
    @RequirePermission("risk:edit")
    public IssueVO updateIssueStatus(Long issueId, IssueStatusUpdateRequest request) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(1091, "问题不存在");
        }
        issue.setStatus(request.getStatus());
        if ("resolved".equals(request.getStatus())) {
            issue.setResolvedAt(LocalDateTime.now());
            issue.setResolvedBy(UserContext.getUserId());
            issue.setResolution(request.getResolution());
        } else if ("closed".equals(request.getStatus())) {
            issue.setClosedAt(LocalDateTime.now());
        }
        issueMapper.updateById(issue);
        return toIssueVO(issue);
    }

    @Transactional
    @RequirePermission("risk:delete")
    public void deleteIssue(Long issueId) {
        issueMapper.deleteById(issueId);
    }

    // ==================== 预警 ====================

    public List<RiskVO> getOverdueRisks(Long projectId) {
        List<Risk> risks = riskMapper.selectList(
                new LambdaQueryWrapper<Risk>()
                        .eq(Risk::getProjectId, projectId)
                        .in(Risk::getStatus, "open", "monitoring")
                        .le(Risk::getReviewDate, LocalDate.now())
                        .orderByAsc(Risk::getReviewDate));
        return risks.stream().map(this::toRiskVO).collect(Collectors.toList());
    }

    public List<IssueVO> getOverdueIssues(Long projectId) {
        List<Issue> issues = issueMapper.selectList(
                new LambdaQueryWrapper<Issue>()
                        .eq(Issue::getProjectId, projectId)
                        .in(Issue::getStatus, "open", "in_progress")
                        .le(Issue::getDueDate, LocalDate.now())
                        .orderByAsc(Issue::getDueDate));
        return issues.stream().map(this::toIssueVO).collect(Collectors.toList());
    }

    // ==================== 内部方法 ====================

    private String calculateRiskLevel(String probability, String impact) {
        if (probability == null || impact == null) return "medium";

        int probScore = switch (probability) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 2;
        };
        int impactScore = switch (impact) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 2;
        };

        int total = probScore * impactScore;
        if (total >= 6) return "critical";
        if (total >= 4) return "high";
        if (total >= 2) return "medium";
        return "low";
    }

    private RiskVO toRiskVO(Risk risk) {
        RiskVO vo = new RiskVO();
        BeanUtils.copyProperties(risk, vo);
        return vo;
    }

    private IssueVO toIssueVO(Issue issue) {
        IssueVO vo = new IssueVO();
        BeanUtils.copyProperties(issue, vo);
        return vo;
    }
}
