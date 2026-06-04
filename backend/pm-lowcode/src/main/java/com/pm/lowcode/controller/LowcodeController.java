package com.pm.lowcode.controller;

import com.pm.common.result.PageResult;
import com.pm.common.result.Result;
import com.pm.lowcode.dto.*;
import com.pm.lowcode.service.LowcodeService;
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
@Tag(name = "低代码引擎", description = "表单定义、表单实例、流程管理")
public class LowcodeController {

    private final LowcodeService lowcodeService;

    // ==================== 表单定义 ====================

    @PostMapping("/form-definitions")
    @Operation(summary = "创建表单定义")
    public Result<FormDefinitionVO> createFormDefinition(@Valid @RequestBody FormDefinitionRequest request) {
        return Result.ok(lowcodeService.createFormDefinition(request));
    }

    @GetMapping("/form-definitions")
    @Operation(summary = "表单定义列表（分页）")
    public Result<PageResult<FormDefinitionVO>> listFormDefinitions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "项目ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword) {
        return Result.ok(lowcodeService.listFormDefinitions(page, size, projectId, keyword));
    }

    @GetMapping("/form-definitions/{formId}")
    @Operation(summary = "表单定义详情")
    public Result<FormDefinitionVO> getFormDefinition(@PathVariable Long formId) {
        return Result.ok(lowcodeService.getFormDefinition(formId));
    }

    @GetMapping("/form-definitions/key/{formKey}")
    @Operation(summary = "按Key获取表单定义")
    public Result<FormDefinitionVO> getFormDefinitionByKey(@PathVariable String formKey) {
        return Result.ok(lowcodeService.getFormDefinitionByKey(formKey));
    }

    @PutMapping("/form-definitions/{formId}")
    @Operation(summary = "更新表单定义")
    public Result<FormDefinitionVO> updateFormDefinition(@PathVariable Long formId,
                                                         @Valid @RequestBody FormDefinitionRequest request) {
        return Result.ok(lowcodeService.updateFormDefinition(formId, request));
    }

    @PostMapping("/form-definitions/{formId}/new-version")
    @Operation(summary = "创建新版本表单定义")
    public Result<FormDefinitionVO> newVersion(@PathVariable Long formId,
                                               @Valid @RequestBody FormDefinitionRequest request) {
        return Result.ok(lowcodeService.newVersion(formId, request));
    }

    @DeleteMapping("/form-definitions/{formId}")
    @Operation(summary = "删除表单定义")
    public Result<Void> deleteFormDefinition(@PathVariable Long formId) {
        lowcodeService.deleteFormDefinition(formId);
        return Result.ok();
    }

    // ==================== 表单实例 ====================

    @PostMapping("/form-instances")
    @Operation(summary = "提交表单实例")
    public Result<FormInstanceVO> submitFormInstance(@Valid @RequestBody FormInstanceRequest request) {
        return Result.ok(lowcodeService.submitFormInstance(request));
    }

    @GetMapping("/form-instances/{instanceId}")
    @Operation(summary = "表单实例详情")
    public Result<FormInstanceVO> getFormInstance(@PathVariable Long instanceId) {
        return Result.ok(lowcodeService.getFormInstance(instanceId));
    }

    @GetMapping("/form-instances")
    @Operation(summary = "按业务实体查询表单实例")
    public Result<List<FormInstanceVO>> listFormInstances(
            @Parameter(description = "实体类型") @RequestParam String entityType,
            @Parameter(description = "实体ID") @RequestParam Long entityId) {
        return Result.ok(lowcodeService.listFormInstances(entityType, entityId));
    }

    // ==================== 流程管理 ====================

    @PostMapping("/process-instances")
    @Operation(summary = "启动流程")
    public Result<ProcessInstanceVO> startProcess(@Valid @RequestBody ProcessStartRequest request) {
        return Result.ok(lowcodeService.startProcess(request));
    }

    @GetMapping("/process-instances/{processInstanceId}")
    @Operation(summary = "流程实例详情")
    public Result<ProcessInstanceVO> getProcessInstance(@PathVariable String processInstanceId) {
        return Result.ok(lowcodeService.getProcessInstance(processInstanceId));
    }

    @GetMapping("/process-instances")
    @Operation(summary = "按业务实体查询流程实例")
    public Result<List<ProcessInstanceVO>> listProcessInstances(
            @Parameter(description = "实体类型") @RequestParam String entityType,
            @Parameter(description = "实体ID") @RequestParam Long entityId) {
        return Result.ok(lowcodeService.listProcessInstances(entityType, entityId));
    }

    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "完成审批任务")
    public Result<Void> completeTask(@PathVariable String taskId,
                                     @Valid @RequestBody TaskCompleteRequest request) {
        lowcodeService.completeTask(taskId, request);
        return Result.ok();
    }
}
