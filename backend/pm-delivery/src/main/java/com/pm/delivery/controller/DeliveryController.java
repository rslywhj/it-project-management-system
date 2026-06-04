package com.pm.delivery.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.delivery.dto.DeliveryRequest;
import com.pm.delivery.dto.DeliveryReviewRequest;
import com.pm.delivery.dto.DeliveryVO;
import com.pm.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "交付物管理", description = "交付物定义、提交审核、版本管理")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/projects/{projectId}/deliveries")
    @Operation(summary = "创建交付物")
    public Result<DeliveryVO> createDelivery(@PathVariable Long projectId,
                                             @Valid @RequestBody DeliveryRequest request) {
        return Result.ok(deliveryService.createDelivery(projectId, request));
    }

    @GetMapping("/projects/{projectId}/deliveries")
    @Operation(summary = "交付物列表（分页）")
    public Result<PageResult<DeliveryVO>> listDeliveries(
            @PathVariable Long projectId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            @Parameter(description = "里程碑ID筛选") @RequestParam(required = false) Long milestoneId) {
        return Result.ok(deliveryService.listDeliveries(projectId, page, size, keyword, status, milestoneId));
    }

    @GetMapping("/deliveries/{deliveryId}")
    @Operation(summary = "交付物详情")
    public Result<DeliveryVO> getDelivery(@PathVariable Long deliveryId) {
        return Result.ok(deliveryService.getDelivery(deliveryId));
    }

    @PutMapping("/deliveries/{deliveryId}")
    @Operation(summary = "更新交付物")
    public Result<DeliveryVO> updateDelivery(@PathVariable Long deliveryId,
                                             @Valid @RequestBody DeliveryRequest request) {
        return Result.ok(deliveryService.updateDelivery(deliveryId, request));
    }

    @PostMapping("/deliveries/{deliveryId}/submit")
    @Operation(summary = "提交交付物审核")
    public Result<DeliveryVO> submitDelivery(@PathVariable Long deliveryId) {
        return Result.ok(deliveryService.submitDelivery(deliveryId));
    }

    @PostMapping("/deliveries/{deliveryId}/review")
    @Operation(summary = "审核交付物")
    public Result<DeliveryVO> reviewDelivery(@PathVariable Long deliveryId,
                                             @Valid @RequestBody DeliveryReviewRequest request) {
        return Result.ok(deliveryService.reviewDelivery(deliveryId, request));
    }

    @PostMapping("/deliveries/{deliveryId}/new-version")
    @Operation(summary = "创建新版本交付物")
    public Result<DeliveryVO> newVersion(@PathVariable Long deliveryId,
                                         @Valid @RequestBody DeliveryRequest request) {
        return Result.ok(deliveryService.newVersion(deliveryId, request));
    }

    @DeleteMapping("/deliveries/{deliveryId}")
    @Operation(summary = "删除交付物")
    public Result<Void> deleteDelivery(@PathVariable Long deliveryId) {
        deliveryService.deleteDelivery(deliveryId);
        return Result.ok();
    }

    @GetMapping("/projects/{projectId}/milestones/{milestoneId}/deliveries")
    @Operation(summary = "按里程碑查询交付物")
    public Result<List<DeliveryVO>> listByMilestone(@PathVariable Long projectId,
                                                     @PathVariable Long milestoneId) {
        return Result.ok(deliveryService.listByMilestone(projectId, milestoneId));
    }
}
