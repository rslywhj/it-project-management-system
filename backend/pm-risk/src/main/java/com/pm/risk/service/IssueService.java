package com.pm.risk.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.risk.domain.Issue;
import com.pm.risk.dto.*;
import com.pm.risk.enums.IssueStatus;
import com.pm.risk.mapper.IssueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueMapper issueMapper;

    private static final ResultCode ISSUE_NOT_FOUND = ResultCode.ISSUE_NOT_FOUND;
    private static final ResultCode ISSUE_STATUS_INVALID = ResultCode.ISSUE_STATUS_INVALID;

    @Transactional
    @RequirePermission("issue:create")
    public IssueVO createIssue(Long projectId, IssueCreateRequest request) {
        Issue issue = new Issue();
        BeanUtils.copyProperties(request, issue);
        issue.setProjectId(projectId);
        issue.setStatus(IssueStatus.OPEN.getCode());
        issue.setReportedBy(UserContext.getUserId());
        if (issue.getIdentifiedDate() == null) {
            issue.setIdentifiedDate(LocalDate.now());
        }

        issueMapper.insert(issue);
        return toVO(issue);
    }

    public PageResult<IssueVO> listIssues(Long projectId, int page, int size,
                                           String keyword, String status, String severity, Long assignedTo) {
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), Issue::getTitle, keyword)
                .eq(StringUtils.hasText(status), Issue::getStatus, status)
                .eq(StringUtils.hasText(severity), Issue::getSeverity, severity)
                .eq(assignedTo != null, Issue::getAssignedTo, assignedTo)
                .orderByDesc(Issue::getCreatedAt);

        Page<Issue> result = issueMapper.selectPage(new Page<>(page, size), wrapper);
        List<IssueVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public IssueVO getIssue(Long issueId) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ISSUE_NOT_FOUND);
        }
        return toVO(issue);
    }

    @Transactional
    @RequirePermission("issue:update")
    public IssueVO updateIssue(Long issueId, IssueUpdateRequest request) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ISSUE_NOT_FOUND);
        }
        BeanUtils.copyProperties(request, issue, "id", "projectId", "status", "resolution",
                "actualResolutionDate", "createdAt", "createdBy");
        issueMapper.updateById(issue);
        return toVO(issue);
    }

    @Transactional
    @RequirePermission("issue:update")
    public IssueVO updateIssueStatus(Long issueId, IssueStatusUpdateRequest request) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ISSUE_NOT_FOUND);
        }

        String currentStatus = issue.getStatus();
        String targetStatus = request.getStatus();

        if (!IssueStatus.isValidTransition(currentStatus, targetStatus)) {
            throw new BusinessException(ISSUE_STATUS_INVALID);
        }

        issue.setStatus(targetStatus);

        // 如果解决问题，记录解决信息
        if (IssueStatus.RESOLVED.getCode().equals(targetStatus)) {
            issue.setActualResolutionDate(LocalDate.now());
            if (StringUtils.hasText(request.getResolution())) {
                issue.setResolution(request.getResolution());
            }
        }

        issueMapper.updateById(issue);
        return toVO(issue);
    }

    @Transactional
    @RequirePermission("issue:update")
    public IssueVO assignIssue(Long issueId, Long assigneeId) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ISSUE_NOT_FOUND);
        }

        issue.setAssignedTo(assigneeId);
        // 如果是待处理状态，自动转为处理中
        if (IssueStatus.OPEN.getCode().equals(issue.getStatus())) {
            issue.setStatus(IssueStatus.IN_PROGRESS.getCode());
        }

        issueMapper.updateById(issue);
        return toVO(issue);
    }

    @Transactional
    @RequirePermission("issue:delete")
    public void deleteIssue(Long issueId) {
        Issue issue = issueMapper.selectById(issueId);
        if (issue == null) {
            throw new BusinessException(ISSUE_NOT_FOUND);
        }
        issueMapper.deleteById(issueId);
    }

    /**
     * 获取即将到期的问题列表
     */
    public List<IssueVO> getExpiringIssues(Long projectId, int daysThreshold) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, projectId)
                .ne(Issue::getStatus, IssueStatus.CLOSED.getCode())
                .ne(Issue::getStatus, IssueStatus.RESOLVED.getCode())
                .isNotNull(Issue::getTargetResolutionDate)
                .le(Issue::getTargetResolutionDate, thresholdDate)
                .ge(Issue::getTargetResolutionDate, LocalDate.now())
                .orderByAsc(Issue::getTargetResolutionDate);

        return issueMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 获取已逾期的问题列表
     */
    public List<IssueVO> getOverdueIssues(Long projectId) {
        LambdaQueryWrapper<Issue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Issue::getProjectId, projectId)
                .ne(Issue::getStatus, IssueStatus.CLOSED.getCode())
                .ne(Issue::getStatus, IssueStatus.RESOLVED.getCode())
                .isNotNull(Issue::getTargetResolutionDate)
                .lt(Issue::getTargetResolutionDate, LocalDate.now())
                .orderByAsc(Issue::getTargetResolutionDate);

        return issueMapper.selectList(wrapper).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private IssueVO toVO(Issue issue) {
        IssueVO vo = new IssueVO();
        BeanUtils.copyProperties(issue, vo);
        return vo;
    }
}
