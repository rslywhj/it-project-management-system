package com.pm.promotion.controller;

import com.pm.common.result.Result;
import com.pm.promotion.dto.PromotionStageTemplateCreateRequest;
import com.pm.promotion.dto.PromotionStageTemplateUpdateRequest;
import com.pm.promotion.dto.PromotionStageTemplateVO;
import com.pm.promotion.service.PromotionStageTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "推广阶段模板管理", description = "推广阶段模板 CRUD、默认模板初始化")
public class PromotionStageTemplateController {

    private final PromotionStageTemplateService templateService;

    @PostMapping("/projects/{projectId}/stage-templates")
    @Operation(summary = "创建推广阶段模板")
    public Result<PromotionStageTemplateVO> createTemplate(@PathVariable Long projectId,
                                                            @Valid @RequestBody PromotionStageTemplateCreateRequest request) {
        return Result.ok(templateService.createTemplate(projectId, request));
    }

    @GetMapping("/projects/{projectId}/stage-templates")
    @Operation(summary = "获取项目阶段模板列表（含全局模板）")
    public Result<List<PromotionStageTemplateVO>> listTemplates(@PathVariable Long projectId) {
        return Result.ok(templateService.listTemplates(projectId));
    }

    @GetMapping("/stage-templates/{templateId}")
    @Operation(summary = "获取阶段模板详情")
    public Result<PromotionStageTemplateVO> getTemplate(@PathVariable Long templateId) {
        return Result.ok(templateService.getTemplate(templateId));
    }

    @PutMapping("/stage-templates/{templateId}")
    @Operation(summary = "更新阶段模板")
    public Result<PromotionStageTemplateVO> updateTemplate(@PathVariable Long templateId,
                                                            @Valid @RequestBody PromotionStageTemplateUpdateRequest request) {
        return Result.ok(templateService.updateTemplate(templateId, request));
    }

    @DeleteMapping("/stage-templates/{templateId}")
    @Operation(summary = "删除阶段模板")
    public Result<Void> deleteTemplate(@PathVariable Long templateId) {
        templateService.deleteTemplate(templateId);
        return Result.ok();
    }

    @PostMapping("/projects/{projectId}/stage-templates/init-default")
    @Operation(summary = "初始化默认阶段模板（从全局模板复制到项目）")
    public Result<List<PromotionStageTemplateVO>> initDefaultTemplates(@PathVariable Long projectId) {
        return Result.ok(templateService.initDefaultTemplates(projectId));
    }
}
