package com.pm.test.dto;

import com.pm.common.validation.ValidationPatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "缺陷请求")
public class BugRequest {

    @NotBlank(message = "缺陷标题不能为空")
    @Size(max = 200, message = "缺陷标题不能超过200个字符")
    @Schema(description = "缺陷标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "缺陷描述")
    private String description;

    @Schema(description = "复现步骤")
    private String stepsToReproduce;

    @Schema(description = "预期结果")
    private String expectedResult;

    @Schema(description = "实际结果")
    private String actualResult;

    @Pattern(regexp = ValidationPatterns.SEVERITY, message = "严重程度必须为 critical/major/minor/trivial")
    @Schema(description = "严重程度（critical/major/minor/trivial）")
    private String severity;

    @Pattern(regexp = ValidationPatterns.PRIORITY, message = "优先级必须为 critical/high/medium/low")
    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "类型（functional/ui/performance/security/other）")
    private String type;

    @Schema(description = "指派给")
    private Long assignedTo;

    @Schema(description = "关联测试计划ID")
    private Long testPlanId;

    @Schema(description = "关联测试用例ID")
    private Long testCaseId;

    @Schema(description = "关联需求ID")
    private Long requirementId;

    @Schema(description = "环境信息")
    private String environment;
}
