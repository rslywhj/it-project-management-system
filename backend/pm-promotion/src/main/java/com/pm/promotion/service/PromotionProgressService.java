package com.pm.promotion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.ResultCode;
import com.pm.promotion.domain.PromotionProgress;
import com.pm.promotion.domain.PromotionStageTemplate;
import com.pm.promotion.domain.PromotionUnit;
import com.pm.promotion.dto.PromotionProgressVO;
import com.pm.promotion.dto.ProgressAdvanceRequest;
import com.pm.promotion.dto.ProgressUpdateRequest;
import com.pm.promotion.mapper.PromotionProgressMapper;
import com.pm.promotion.mapper.PromotionStageTemplateMapper;
import com.pm.promotion.mapper.PromotionUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionProgressService {

    private final PromotionProgressMapper progressMapper;
    private final PromotionStageTemplateMapper stageTemplateMapper;
    private final PromotionUnitMapper unitMapper;

    /**
     * 获取推广单元的进度列表
     */
    public List<PromotionProgressVO> listProgress(Long unitId) {
        List<PromotionProgress> progressList = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId)
                        .orderByAsc(PromotionProgress::getId));

        List<Long> templateIds = progressList.stream()
                .map(PromotionProgress::getStageTemplateId)
                .collect(Collectors.toList());
        Map<Long, PromotionStageTemplate> templateMap = stageTemplateMapper.selectBatchIds(templateIds)
                .stream()
                .collect(Collectors.toMap(PromotionStageTemplate::getId, t -> t));

        return progressList.stream().map(p -> {
            PromotionProgressVO vo = new PromotionProgressVO();
            BeanUtils.copyProperties(p, vo);
            PromotionStageTemplate tpl = templateMap.get(p.getStageTemplateId());
            if (tpl != null) {
                vo.setStageTemplateName(tpl.getName());
                vo.setSortOrder(tpl.getSortOrder());
                vo.setIsLocked(tpl.getIsLocked());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 推进到下一阶段
     */
    @Transactional
    @RequirePermission("promotion:edit")
    public PromotionProgressVO advanceStage(Long progressId, ProgressAdvanceRequest request) {
        PromotionProgress progress = progressMapper.selectById(progressId);
        if (progress == null) {
            throw new BusinessException(ResultCode.PROGRESS_NOT_FOUND);
        }

        // 检查当前阶段是否锁定
        PromotionStageTemplate currentTemplate = stageTemplateMapper.selectById(progress.getStageTemplateId());
        if (currentTemplate != null && Boolean.TRUE.equals(currentTemplate.getIsLocked())) {
            // 锁定阶段必须完成，不能跳过
            if (request.getCompletionRate() == null ||
                    request.getCompletionRate().compareTo(BigDecimal.valueOf(100)) < 0) {
                throw new BusinessException(ResultCode.PROGRESS_STAGE_LOCKED);
            }
        }

        // 更新当前阶段
        if (request.getCompletionRate() != null) {
            progress.setCompletionRate(request.getCompletionRate());
        }
        if (request.getRemark() != null) {
            progress.setRemark(request.getRemark());
        }

        // 判断是否完成
        if (progress.getCompletionRate().compareTo(BigDecimal.valueOf(100)) >= 0) {
            progress.setStatus("completed");
            progress.setCompletedAt(LocalDateTime.now());
        } else if (progress.getCompletionRate().compareTo(BigDecimal.ZERO) > 0) {
            progress.setStatus("in_progress");
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
        }

        progressMapper.updateById(progress);

        // 更新推广单元整体完成率和当前阶段
        updateUnitProgress(progress.getPromotionUnitId());

        return toVO(progress);
    }

    /**
     * 更新进度
     */
    @Transactional
    @RequirePermission("promotion:edit")
    public PromotionProgressVO updateProgress(Long progressId, ProgressUpdateRequest request) {
        PromotionProgress progress = progressMapper.selectById(progressId);
        if (progress == null) {
            throw new BusinessException(ResultCode.PROGRESS_NOT_FOUND);
        }

        if (request.getCompletionRate() != null) {
            progress.setCompletionRate(request.getCompletionRate());
        }
        if (request.getExpectedEndDate() != null) {
            progress.setExpectedEndDate(request.getExpectedEndDate());
        }
        if (request.getRemark() != null) {
            progress.setRemark(request.getRemark());
        }

        // 自动更新状态
        if (progress.getCompletionRate().compareTo(BigDecimal.valueOf(100)) >= 0) {
            progress.setStatus("completed");
            progress.setCompletedAt(LocalDateTime.now());
        } else if (progress.getCompletionRate().compareTo(BigDecimal.ZERO) > 0) {
            progress.setStatus("in_progress");
            if (progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
        }

        progressMapper.updateById(progress);

        // 更新推广单元整体进度
        updateUnitProgress(progress.getPromotionUnitId());

        return toVO(progress);
    }

    /**
     * 更新推广单元的整体完成率和当前阶段
     */
    private void updateUnitProgress(Long unitId) {
        List<PromotionProgress> allProgress = progressMapper.selectList(
                new LambdaQueryWrapper<PromotionProgress>()
                        .eq(PromotionProgress::getPromotionUnitId, unitId));

        if (allProgress.isEmpty()) return;

        // 计算平均完成率
        BigDecimal totalRate = allProgress.stream()
                .map(PromotionProgress::getCompletionRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgRate = totalRate.divide(BigDecimal.valueOf(allProgress.size()), 2, RoundingMode.HALF_UP);

        // 找到当前阶段（最后一个进行中或第一个未开始的）
        PromotionProgress currentStage = allProgress.stream()
                .filter(p -> "in_progress".equals(p.getStatus()))
                .findFirst()
                .orElse(allProgress.stream()
                        .filter(p -> "pending".equals(p.getStatus()))
                        .findFirst()
                        .orElse(null));

        PromotionUnit unit = unitMapper.selectById(unitId);
        if (unit != null) {
            unit.setCompletionRate(avgRate);
            if (currentStage != null) {
                unit.setCurrentStageId(currentStage.getStageTemplateId());
            }

            // 判断整体状态
            boolean allCompleted = allProgress.stream()
                    .allMatch(p -> "completed".equals(p.getStatus()) || "skipped".equals(p.getStatus()));
            if (allCompleted) {
                unit.setStatus("completed");
                unit.setActualEndDate(LocalDate.now());
            } else if (avgRate.compareTo(BigDecimal.ZERO) > 0) {
                unit.setStatus("in_progress");
                if (unit.getActualStartDate() == null) {
                    unit.setActualStartDate(LocalDate.now());
                }
            }

            unitMapper.updateById(unit);
        }
    }

    private PromotionProgressVO toVO(PromotionProgress progress) {
        PromotionProgressVO vo = new PromotionProgressVO();
        BeanUtils.copyProperties(progress, vo);

        PromotionStageTemplate tpl = stageTemplateMapper.selectById(progress.getStageTemplateId());
        if (tpl != null) {
            vo.setStageTemplateName(tpl.getName());
            vo.setSortOrder(tpl.getSortOrder());
            vo.setIsLocked(tpl.getIsLocked());
        }
        return vo;
    }
}
