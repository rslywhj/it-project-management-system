package com.pm.delivery.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pm.auth.annotation.RequirePermission;
import com.pm.common.exception.BusinessException;
import com.pm.common.result.PageResult;
import com.pm.common.result.ResultCode;
import com.pm.common.util.UserContext;
import com.pm.delivery.domain.Delivery;
import com.pm.delivery.dto.DeliveryRequest;
import com.pm.delivery.dto.DeliveryReviewRequest;
import com.pm.delivery.dto.DeliveryVO;
import com.pm.delivery.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryMapper deliveryMapper;

    @Transactional
    @RequirePermission("delivery:create")
    public DeliveryVO createDelivery(Long projectId, DeliveryRequest request) {
        Delivery delivery = new Delivery();
        BeanUtils.copyProperties(request, delivery);
        delivery.setProjectId(projectId);
        delivery.setStatus("draft");
        delivery.setVersion(1);
        deliveryMapper.insert(delivery);
        return toVO(delivery);
    }

    public PageResult<DeliveryVO> listDeliveries(Long projectId, int page, int size,
                                                  String keyword, String status, Long milestoneId) {
        LambdaQueryWrapper<Delivery> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Delivery::getProjectId, projectId)
                .like(StringUtils.hasText(keyword), Delivery::getName, keyword)
                .eq(StringUtils.hasText(status), Delivery::getStatus, status)
                .eq(milestoneId != null, Delivery::getMilestoneId, milestoneId)
                .orderByDesc(Delivery::getCreatedAt);

        Page<Delivery> result = deliveryMapper.selectPage(new Page<>(page, size), wrapper);
        List<DeliveryVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return PageResult.of(voList, result.getTotal(), page, size);
    }

    public DeliveryVO getDelivery(Long deliveryId) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new BusinessException(1070, "交付物不存在");
        }
        return toVO(delivery);
    }

    @Transactional
    @RequirePermission("delivery:edit")
    public DeliveryVO updateDelivery(Long deliveryId, DeliveryRequest request) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new BusinessException(1070, "交付物不存在");
        }
        // 只有草稿状态才能编辑
        if (!"draft".equals(delivery.getStatus()) && !"rejected".equals(delivery.getStatus())) {
            throw new BusinessException(1071, "只有草稿或驳回状态的交付物才能编辑");
        }
        BeanUtils.copyProperties(request, delivery, "id", "projectId", "status", "version",
                "submittedBy", "reviewedBy", "reviewedAt", "reviewComment",
                "createdAt", "createdBy", "isDeleted");
        deliveryMapper.updateById(delivery);
        return toVO(delivery);
    }

    @Transactional
    @RequirePermission("delivery:create")
    public DeliveryVO submitDelivery(Long deliveryId) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new BusinessException(1070, "交付物不存在");
        }
        if (!"draft".equals(delivery.getStatus()) && !"rejected".equals(delivery.getStatus())) {
            throw new BusinessException(1072, "只有草稿或驳回状态的交付物才能提交");
        }
        delivery.setStatus("submitted");
        delivery.setSubmittedBy(UserContext.getUserId());
        deliveryMapper.updateById(delivery);
        return toVO(delivery);
    }

    @Transactional
    @RequirePermission("delivery:manage")
    public DeliveryVO reviewDelivery(Long deliveryId, DeliveryReviewRequest request) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null) {
            throw new BusinessException(1070, "交付物不存在");
        }
        if (!"submitted".equals(delivery.getStatus())) {
            throw new BusinessException(1073, "只有已提交状态的交付物才能审核");
        }

        if ("approve".equals(request.getAction())) {
            delivery.setStatus("approved");
        } else if ("reject".equals(request.getAction())) {
            delivery.setStatus("rejected");
        } else {
            throw new BusinessException(400, "无效的审核动作");
        }

        delivery.setReviewedBy(UserContext.getUserId());
        delivery.setReviewedAt(LocalDateTime.now());
        delivery.setReviewComment(request.getReviewComment());
        deliveryMapper.updateById(delivery);
        return toVO(delivery);
    }

    @Transactional
    @RequirePermission("delivery:create")
    public DeliveryVO newVersion(Long deliveryId, DeliveryRequest request) {
        Delivery oldDelivery = deliveryMapper.selectById(deliveryId);
        if (oldDelivery == null) {
            throw new BusinessException(1070, "交付物不存在");
        }

        // 创建新版本
        Delivery newDelivery = new Delivery();
        BeanUtils.copyProperties(request, newDelivery);
        newDelivery.setProjectId(oldDelivery.getProjectId());
        newDelivery.setMilestoneId(oldDelivery.getMilestoneId());
        newDelivery.setPromotionUnitId(oldDelivery.getPromotionUnitId());
        newDelivery.setStatus("draft");
        newDelivery.setVersion(oldDelivery.getVersion() + 1);
        deliveryMapper.insert(newDelivery);
        return toVO(newDelivery);
    }

    @Transactional
    @RequirePermission("delivery:delete")
    public void deleteDelivery(Long deliveryId) {
        deliveryMapper.deleteById(deliveryId);
    }

    public List<DeliveryVO> listByMilestone(Long projectId, Long milestoneId) {
        List<Delivery> deliveries = deliveryMapper.selectList(
                new LambdaQueryWrapper<Delivery>()
                        .eq(Delivery::getProjectId, projectId)
                        .eq(Delivery::getMilestoneId, milestoneId)
                        .orderByDesc(Delivery::getCreatedAt));
        return deliveries.stream().map(this::toVO).collect(Collectors.toList());
    }

    private DeliveryVO toVO(Delivery delivery) {
        DeliveryVO vo = new DeliveryVO();
        BeanUtils.copyProperties(delivery, vo);
        return vo;
    }
}
